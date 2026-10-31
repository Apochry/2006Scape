package com.rs2.tools.mapgen;
public class InvCompare { public static void main(String[] a) throws Exception { try(CacheEditor c=new CacheEditor(java.nio.file.Paths.get(a[0]))){ ModelForge mf=new ModelForge(c); ArmourForge af=new ArmourForge(mf);
 double T=Math.PI*2/2048; Sheet s=new Sheet(200,200,5); ArmourForge.Palette p=ArmourForge.WARDEN;
 int[][] it={{2813,160,152},{2605,488,0},{2582,444,0},{2339,344,1104},{5037,164,156}};
 ModelData[] mine={af.inventory(af.helm(p),218,2813),af.inventory(ArmourForge.mergeAll(af.body(p)),new int[]{306,164},2605),af.inventory(af.legs(p),268,2582),af.shieldInventory(af.shield(p),-1104*T,344*T),af.inventory(af.boots(p),4954,5037)};
 for(int i=0;i<5;i++) s.add("rune inv "+it[i][0],Sheet.recolour(mf.read(it[i][0]),61,36133,41,37270,57,7054,926,48030,24,61),-it[i][2]*T,it[i][1]*T);
 for(int i=0;i<5;i++) s.add("warden inv",mine[i],-it[i][2]*T,it[i][1]*T);
 s.write(a[1]); }}}
