package com.rs2.tools.mapgen;
import java.util.*;
public class Dbg { public static void main(String[] a) throws Exception { try(CacheEditor c=new CacheEditor(java.nio.file.Paths.get(a[0]))){ ModelForge f=new ModelForge(c);
 for(int i=1;i<a.length;i+=2){ ModelData m=f.read(Integer.parseInt(a[i])); int col=Integer.parseInt(a[i+1]); int[] b={999,999,999,-999,-999,-999}; TreeMap<Integer,Integer> sk=new TreeMap<>(); TreeMap<Integer,Integer> pr=new TreeMap<>();
  for(int fi=0;fi<m.faces.size();fi++) if(m.colors.get(fi)==col){ pr.merge(m.priorities.get(fi),1,Integer::sum); for(int v:m.faces.get(fi)){ int[] p=m.vertices.get(v); sk.merge(m.vertexSkins.get(v),1,Integer::sum); for(int k=0;k<3;k++){b[k]=Math.min(b[k],p[k]);b[k+3]=Math.max(b[k+3],p[k]);}}}
  System.out.println(a[i]+" colour "+col+" bounds "+Arrays.toString(b)+" skins "+sk+" prio "+pr); }
}}}
