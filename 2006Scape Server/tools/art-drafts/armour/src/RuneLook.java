package com.rs2.tools.mapgen;
public class RuneLook { public static void main(String[] a) throws Exception {
 try (CacheEditor c = new CacheEditor(java.nio.file.Paths.get(a[0]))) { ModelForge f = new ModelForge(c);
  Sheet s=new Sheet(260,300,6);
  int[][] items={{218,61,36133,926,48030},{306,24,61,61,36133,41,37270},{164,61,36133,41,37270},{268,61,36133,41,37270,57,36133},{486,61,36133,57,7054},{4954,61,36133}};
  for(int[] it:items){ for(double yaw: new double[]{0,1.57,3.14}){ ModelData m=f.read(it[0]); int[] p=java.util.Arrays.copyOfRange(it,1,it.length); Sheet.recolour(m,p); s.add(it[0]+" yaw"+yaw,m,yaw,0.15);} }
  s.write(a[1]); }}}
