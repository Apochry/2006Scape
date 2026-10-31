package com.rs2.tools.mapgen;
import java.util.*;
/** Builds both palettes of the quest armour, round-trips every model through encode/decode, renders the sheets. */
public class Build {
 static final double TAU=Math.PI*2;
 static ModelData skinMap(ModelData m){ ModelData c=Anim.copy(m); for(int i=0;i<c.faces.size();i++){int s=c.vertexSkins.get(c.faces.get(i)[0]); c.colors.set(i,ModelData.hsl((s*23+5)%64,6,48+(s%3)*14));} return c;}
 static ModelData check(String name, ModelData m, List<String> rep){ byte[] b=m.encode(); ModelData d=ModelData.decode(b);
  if(d.vertices.size()!=m.vertices.size()||d.faces.size()!=m.faces.size()) throw new IllegalStateException(name+" round-trip mismatch");
  for(int i=0;i<m.vertices.size();i++){ if(!Arrays.equals(m.vertices.get(i),d.vertices.get(i))||!m.vertexSkins.get(i).equals(d.vertexSkins.get(i))) throw new IllegalStateException(name+" vertex "+i+" differs"); }
  for(int i=0;i<m.faces.size();i++){ if(!Arrays.equals(m.faces.get(i),d.faces.get(i))||!m.colors.get(i).equals(d.colors.get(i))||!m.priorities.get(i).equals(d.priorities.get(i))) throw new IllegalStateException(name+" face "+i+" differs"); }
  TreeSet<Integer> sk=new TreeSet<>(m.vertexSkins); TreeSet<Integer> cols=new TreeSet<>(m.colors);
  rep.add(String.format("%-34s v=%3d  tris=%3d  bytes=%5d  colours=%d  skins=%s", name, m.vertices.size(), m.faces.size(), b.length, cols.size(), sk)); return d; }
 public static void main(String[] a) throws Exception { try(CacheEditor c=new CacheEditor(java.nio.file.Paths.get(a[0]))){ ModelForge mf=new ModelForge(c); ArmourForge af=new ArmourForge(mf); Anim an=new Anim(c);
  String out=a[1]; List<String> rep=new ArrayList<>();
  Sheet sheet=new Sheet(230,280,6);
  Sheet outfit=new Sheet(230,300,8);
  for(ArmourForge.Palette p: new ArmourForge.Palette[]{ArmourForge.WARDEN, ArmourForge.ROYAL}){
   boolean w=p==ArmourForge.WARDEN; String tag=w?"Warden":"Royal Guard";
   af.skinLog.add("--- "+p.name);
   ModelData helm=check(tag+" full helm (worn)",af.helm(p),rep);
   ModelData[] ba=af.body(p); ModelData body=check(tag+" platebody (worn)",ba[0],rep), arms=check(tag+" platebody sleeves (worn #2)",ba[1],rep);
   ModelData legs=check(tag+" platelegs (worn)",af.legs(p),rep);
   ModelData kite=check(tag+" kiteshield (worn)",af.shield(p),rep);
   ModelData boots=check(tag+" boots (worn)",af.boots(p),rep);
   ModelData gl=check(tag+" gauntlets (worn)",af.gauntlets(p),rep);
   ModelData iHelm=check(tag+" full helm (inventory)",af.inventory(helm,218,2813),rep), iBody=check(tag+" platebody (inventory)",af.inventory(ArmourForge.mergeAll(body,arms),new int[]{306,164},2605),rep),
    iLegs=check(tag+" platelegs (inventory)",af.inventory(legs,268,2582),rep), iKite=check(tag+" kiteshield (inventory)",af.shieldInventory(kite,-1104*TAU/2048,344*TAU/2048),rep),
    iBoots=check(tag+" boots (inventory)",af.inventory(boots,4954,5037),rep), iGl=check(tag+" gauntlets (inventory)",af.gauntletsInventory(gl),rep);
   if(w){
    Object[][] rows={{"helm",helm,iHelm,160,152},{"platebody",ArmourForge.mergeAll(body,arms),iBody,488,0},{"platelegs",legs,iLegs,444,0},{"kiteshield",kite,iKite,344,1104},{"boots",boots,iBoots,164,156},{"gauntlets",gl,iGl,420,0}};
    for(Object[] r:rows){ ModelData m=(ModelData)r[1]; String n=tag+" "+r[0];
     sheet.add(n+" front",m,0,0.12); sheet.add(n+" 3/4",m,0.75,0.25); if(r[0].equals("kiteshield")) sheet.add(n+" outer face",m,-1.57,0.1); else sheet.add(n+" side",m,1.57,0.1); sheet.add(n+" back",m,2.6,0.2);
     sheet.add(n+" inventory",(ModelData)r[2],-(Integer)r[4]*TAU/2048,(Integer)r[3]*TAU/2048); sheet.add(r[0]+" skin labels",skinMap(m),0.75,0.25);}
   } else {
    Object[][] rows={{"helm",helm,iHelm,160,152},{"platebody",ArmourForge.mergeAll(body,arms),iBody,488,0},{"kiteshield",kite,iKite,344,1104}};
    for(Object[] r:rows){ ModelData m=(ModelData)r[1]; String n=tag+" "+r[0];
     sheet.add(n+" front",m,0,0.12); sheet.add(n+" 3/4",m,r[0].equals("kiteshield")?-0.9:0.75,0.25); sheet.add(n+" back",m,2.6,0.2); sheet.add(n+" inventory",(ModelData)r[2],-(Integer)r[4]*TAU/2048,(Integer)r[3]*TAU/2048);}
    sheet.add(tag+" platelegs inv",iLegs,0,444*TAU/2048); sheet.add(tag+" boots inv",iBoots,-156*TAU/2048,164*TAU/2048);
   }
   ModelData player=ArmourForge.mergeAll(helm,body,arms,legs,kite,gl,boots);
   double s=1.25;
   outfit.add(tag+" front",player,0,0.15,s); outfit.add(tag+" 3/4",player,0.7,0.25,s); outfit.add(tag+" shield side",player,-0.9,0.2,s); outfit.add(tag+" back",player,3.14,0.15,s);
   int[][] poses={{819,2},{819,6},{451,3},{1156,6}}; String[] pn={"walk","walk","slash","block"};
   for(int i=0;i<poses.length;i++){ outfit.add(tag+" "+pn[i]+" (seq "+poses[i][0]+")",Anim.pose(player,an.frames.get(an.seqFrames.get(poses[i][0])[poses[i][1]])),i==3?-0.6:0.6,0.2,s); }
   Sheet.class.getName();
  }
  sheet.write(out+"/pieces.png"); outfit.write(out+"/outfit.png");
  for(String r:rep) System.out.println(r); System.out.println(); for(String r:af.skinLog) System.out.println(r);
 }}}
