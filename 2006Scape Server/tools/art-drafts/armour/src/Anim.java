package com.rs2.tools.mapgen;
import java.nio.ByteBuffer; import java.util.*;
/**
 * Minimal 317 animation support (seq.dat sequences + idx2 frame files with their skeletons), applying frames to a
 * ModelData exactly like the client's Model.applyTransform: labels -> vertex groups, transform types
 * 0 origin, 1 translate, 2 rotate, 3 scale. Used to check worn armour deforms with the player's animations.
 */
public class Anim {
 static final class Skel { int[] types; int[][] labels; }
 static final class Frame { Skel skel; int[] idx, dx, dy, dz; int delay; }
 final Map<Integer,Frame> frames = new HashMap<>(); int failed;
 final Map<Integer,int[]> seqFrames = new HashMap<>();
 static final int[] SIN=new int[2048], COS=new int[2048];
 static { for(int i=0;i<2048;i++){SIN[i]=(int)(65536*Math.sin(i*0.0030679615)); COS[i]=(int)(65536*Math.cos(i*0.0030679615));} }

 Anim(CacheEditor c) throws Exception {
  CacheEditor.Archive cfg = CacheEditor.Archive.decode(c.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
  ByteBuffer b = ByteBuffer.wrap(cfg.get("seq.dat")); int n=b.getShort()&0xFFFF;
  for(int s=0;s<n;s++){ int[] fr=null; while(true){int op=b.get()&255; if(op==0)break;
    if(op==1){int k=b.get()&255; fr=new int[k]; for(int i=0;i<k;i++){fr[i]=b.getShort()&0xFFFF; b.getShort(); b.getShort();}}
    else if(op==2||op==6||op==7) b.getShort(); else if(op==3){int k=b.get()&255; b.position(b.position()+k);} else if(op==4){} else if(op==5||op==8||op==9||op==10||op==11) b.get(); else if(op==12) b.getInt();
    else throw new IllegalStateException("seq op "+op+" in "+s);}
   if(fr!=null) seqFrames.put(s,fr);}
  int files=c.fileCount(2);
  for(int f=0;f<files;f++){ byte[] raw=c.read(2,f); if(raw==null||raw.length<10) continue;
   try { load(org.apollo.util.CompressionUtil.degzip(ByteBuffer.wrap(raw))); } catch(Exception e){ failed++; if(failed<4) System.err.println("frame file "+f+": "+e); } }
 }
 private static int smart(ByteBuffer b){int v=b.get(b.position())&255; if(v<128){b.get(); return v-64;} return (b.getShort()&0xFFFF)-49152;}
 void load(byte[] d){
  ByteBuffer h=ByteBuffer.wrap(d); h.position(d.length-8); int i=h.getShort()&0xFFFF,j=h.getShort()&0xFFFF,k=h.getShort()&0xFFFF,l=h.getShort()&0xFFFF;
  int o=0; ByteBuffer s1=ByteBuffer.wrap(d); s1.position(o); o+=i+2; ByteBuffer s2=ByteBuffer.wrap(d); s2.position(o); o+=j;
  ByteBuffer s3=ByteBuffer.wrap(d); s3.position(o); o+=k; ByteBuffer s4=ByteBuffer.wrap(d); s4.position(o); o+=l; ByteBuffer s5=ByteBuffer.wrap(d); s5.position(o);
  Skel sk=new Skel(); int n=s5.get()&255; sk.types=new int[n]; sk.labels=new int[n][];
  for(int a=0;a<n;a++) sk.types[a]=s5.get()&255; for(int a=0;a<n;a++){ sk.labels[a]=new int[s5.get()&255]; for(int q=0;q<sk.labels[a].length;q++) sk.labels[a][q]=s5.get()&255; }
  int count=s1.getShort()&0xFFFF;
  for(int fi=0;fi<count;fi++){ int id=s1.getShort()&0xFFFF; Frame fr=new Frame(); fr.skel=sk; fr.delay=s4.get()&255; int nt=s1.get()&255;
   List<int[]> L=new ArrayList<>(); int last=-1;
   for(int t=0;t<nt;t++){ int fl=s2.get()&255; if(fl>0){
     if(sk.types[t]!=0) for(int q=t-1;q>last;q--) if(sk.types[q]==0){ L.add(new int[]{q,0,0,0}); break; }
     int def=sk.types[t]==3?128:0;
     int x=(fl&1)!=0?smart(s3):def, y=(fl&2)!=0?smart(s3):def, z=(fl&4)!=0?smart(s3):def;
     L.add(new int[]{t,x,y,z}); last=t; } }
   fr.idx=new int[L.size()]; fr.dx=new int[L.size()]; fr.dy=new int[L.size()]; fr.dz=new int[L.size()];
   for(int q=0;q<L.size();q++){fr.idx[q]=L.get(q)[0];fr.dx[q]=L.get(q)[1];fr.dy[q]=L.get(q)[2];fr.dz[q]=L.get(q)[3];}
   frames.put(id,fr);} }

 /** Returns a deformed copy of m posed at frame id. */
 static ModelData pose(ModelData src, Frame fr){
  ModelData m=copy(src); int nv=m.vertices.size(); int[] X=new int[nv],Y=new int[nv],Z=new int[nv];
  for(int v=0;v<nv;v++){int[] p=m.vertices.get(v);X[v]=p[0];Y[v]=p[1];Z[v]=p[2];}
  Map<Integer,List<Integer>> groups=new HashMap<>(); for(int v=0;v<nv;v++) groups.computeIfAbsent(m.vertexSkins.get(v),q->new ArrayList<>()).add(v);
  int ox=0,oy=0,oz=0;
  for(int t=0;t<fr.idx.length;t++){ int type=fr.skel.types[fr.idx[t]]; int[] labs=fr.skel.labels[fr.idx[t]]; int j=fr.dx[t],k=fr.dy[t],l=fr.dz[t];
   if(type==0){ int cnt=0; ox=oy=oz=0; for(int lab:labs) for(int v:groups.getOrDefault(lab,List.of())){ox+=X[v];oy+=Y[v];oz+=Z[v];cnt++;}
    if(cnt>0){ox=ox/cnt+j;oy=oy/cnt+k;oz=oz/cnt+l;} else {ox=j;oy=k;oz=l;} }
   else if(type==1){ for(int lab:labs) for(int v:groups.getOrDefault(lab,List.of())){X[v]+=j;Y[v]+=k;Z[v]+=l;} }
   else if(type==2){ for(int lab:labs) for(int v:groups.getOrDefault(lab,List.of())){ X[v]-=ox;Y[v]-=oy;Z[v]-=oz;
     int rx=(j&255)*8, ry=(k&255)*8, rz=(l&255)*8;
     if(rz!=0){int sn=SIN[rz],cs=COS[rz]; int nx=Y[v]*sn+X[v]*cs>>16; Y[v]=Y[v]*cs-X[v]*sn>>16; X[v]=nx;}
     if(rx!=0){int sn=SIN[rx],cs=COS[rx]; int ny=Y[v]*cs-Z[v]*sn>>16; Z[v]=Y[v]*sn+Z[v]*cs>>16; Y[v]=ny;}
     if(ry!=0){int sn=SIN[ry],cs=COS[ry]; int nx=Z[v]*sn+X[v]*cs>>16; Z[v]=Z[v]*cs-X[v]*sn>>16; X[v]=nx;}
     X[v]+=ox;Y[v]+=oy;Z[v]+=oz; } }
   else if(type==3){ for(int lab:labs) for(int v:groups.getOrDefault(lab,List.of())){ X[v]-=ox;Y[v]-=oy;Z[v]-=oz; X[v]=X[v]*j/128;Y[v]=Y[v]*k/128;Z[v]=Z[v]*l/128; X[v]+=ox;Y[v]+=oy;Z[v]+=oz; } }
  }
  for(int v=0;v<nv;v++){int[] p=m.vertices.get(v);p[0]=X[v];p[1]=Y[v];p[2]=Z[v];}
  return m; }
 static ModelData copy(ModelData s){ return ModelData.decode(s.encode()); }
 public static void main(String[] a) throws Exception { try(CacheEditor c=new CacheEditor(java.nio.file.Paths.get(a[0]))){ Anim an=new Anim(c);
  System.out.println("frames "+an.frames.size()+" seqs "+an.seqFrames.size());
  for(int i=1;i<a.length;i++){int s=Integer.parseInt(a[i]); int[] f=an.seqFrames.get(s); System.out.print("seq "+s+":"); if(f!=null) for(int x:f) System.out.print(" "+x+(an.frames.containsKey(x)?"":"(missing)")); System.out.println();} }}
}
