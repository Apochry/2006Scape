package com.rs2.tools.mapgen;
import java.util.*;
public class ModelInfo { public static void main(String[] a) throws Exception {
 try (CacheEditor c = new CacheEditor(java.nio.file.Paths.get(a[0]))) { ModelForge f = new ModelForge(c);
  for (int i=1;i<a.length;i++){ int id=Integer.parseInt(a[i]); ModelData m=f.read(id);
   byte[] raw=org.apollo.util.CompressionUtil.degzip(java.nio.ByteBuffer.wrap(c.read(1,id))); int nt=raw[raw.length-18+4]&255;
   TreeMap<Integer,int[]> sk=new TreeMap<>(); for(int v=0;v<m.vertices.size();v++){int s=m.vertexSkins.get(v); int[] p=m.vertices.get(v);
    int[] b=sk.computeIfAbsent(s,k->new int[]{0,9999,9999,9999,-9999,-9999,-9999}); b[0]++; for(int k=0;k<3;k++){b[1+k]=Math.min(b[1+k],p[k]);b[4+k]=Math.max(b[4+k],p[k]);}}
   TreeMap<Integer,Integer> cols=new TreeMap<>(); for(int col:m.colors) cols.merge(col,1,Integer::sum);
   TreeMap<Integer,Integer> rt=new TreeMap<>(); for(int r:m.renderTypes) rt.merge(r,1,Integer::sum);
   TreeMap<Integer,Integer> pr=new TreeMap<>(); for(int r:m.priorities) pr.merge(r,1,Integer::sum);
   TreeMap<Integer,Integer> al=new TreeMap<>(); for(int r:m.alphas) al.merge(r,1,Integer::sum);
   TreeMap<Integer,Integer> fs=new TreeMap<>(); for(int r:m.faceSkins) fs.merge(r,1,Integer::sum);
   System.out.println("model "+id+" v="+m.vertices.size()+" f="+m.faces.size()+" nt="+nt+" bounds="+Arrays.toString(m.bounds()));
   System.out.println("  colours "+cols+"\n  render "+rt+" prio "+pr+" alpha "+al+" faceSkins "+fs);
   for(var e:sk.entrySet()){int[] b=e.getValue(); System.out.println("  skin "+e.getKey()+" n="+b[0]+" x["+b[1]+","+b[4]+"] y["+b[2]+","+b[5]+"] z["+b[3]+","+b[6]+"]");}
  }}}}
