package com.rs2.tools.mapgen;
import java.nio.ByteBuffer; import java.util.*;
public class LabelBounds { public static void main(String[] a) throws Exception {
 try (CacheEditor c = new CacheEditor(java.nio.file.Paths.get(a[0]))) {
  for (int i=1;i<a.length;i++){ int id=Integer.parseInt(a[i]);
   ModelData m = ModelData.decode(org.apollo.util.CompressionUtil.degzip(ByteBuffer.wrap(c.read(1, id))));
   System.out.println("model "+id+" v="+m.vertices.size()+" f="+m.faces.size());
   TreeMap<Integer,int[]> b=new TreeMap<>(); TreeMap<Integer,Integer> cnt=new TreeMap<>();
   for(int k=0;k<m.vertices.size();k++){int l=m.vertexSkins.get(k); int[] v=m.vertices.get(k); int[] r=b.computeIfAbsent(l,x->new int[]{99999,99999,99999,-99999,-99999,-99999}); for(int q=0;q<3;q++){r[q]=Math.min(r[q],v[q]); r[q+3]=Math.max(r[q+3],v[q]);} cnt.merge(l,1,Integer::sum);}
   for(int l: b.keySet()) System.out.println("  vlabel "+l+" n="+cnt.get(l)+" min="+Arrays.toString(Arrays.copyOf(b.get(l),3))+" max="+Arrays.toString(Arrays.copyOfRange(b.get(l),3,6)));
   TreeMap<Integer,String> fl=new TreeMap<>(); for(int k=0;k<m.faces.size();k++){int l=m.faceSkins.get(k); fl.merge(l, "a"+m.alphas.get(k)+"/p"+m.priorities.get(k)+"/c"+m.colors.get(k)+" ", (x,y)->x.length()>60?x:x+y);}
   for(int l: fl.keySet()) System.out.println("  flabel "+l+": "+fl.get(l));
  }}}}
