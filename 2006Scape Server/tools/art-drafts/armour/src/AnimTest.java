package com.rs2.tools.mapgen;
public class AnimTest { 
 static ModelData merge(ModelData... ms){ ModelData o=new ModelData(); for(ModelData m:ms){ int base=o.vertices.size();
  for(int i=0;i<m.vertices.size();i++){o.vertices.add(m.vertices.get(i).clone()); o.vertexSkins.add(m.vertexSkins.get(i));}
  for(int i=0;i<m.faces.size();i++){int[] f=m.faces.get(i); o.faces.add(new int[]{f[0]+base,f[1]+base,f[2]+base}); o.colors.add(m.colors.get(i)); o.renderTypes.add(m.renderTypes.get(i)); o.priorities.add(m.priorities.get(i)); o.alphas.add(m.alphas.get(i)); o.faceSkins.add(m.faceSkins.get(i));}} return o;}
 public static void main(String[] a) throws Exception { try(CacheEditor c=new CacheEditor(java.nio.file.Paths.get(a[0]))){ Anim an=new Anim(c); ModelForge f=new ModelForge(c);
  ModelData body=merge(Sheet.recolour(f.read(218),61,36133,926,48030), Sheet.recolour(f.read(306),24,61,61,36133,41,37270), Sheet.recolour(f.read(164),61,36133,41,37270), Sheet.recolour(f.read(268),61,36133,41,37270,57,36133), Sheet.recolour(f.read(486),61,36133,57,7054), f.read(176), Sheet.recolour(f.read(4954),61,36133));
  Sheet s=new Sheet(220,300,6); s.add("bind",body,0.5,0.2);
  int[][] tests={{808,0},{819,2},{819,6},{451,3},{1156,6}};
  for(int[] t:tests){ int fid=an.seqFrames.get(t[0])[t[1]]; s.add("seq"+t[0]+" f"+t[1],Anim.pose(body,an.frames.get(fid)),0.5,0.2); }
  s.write(a[1]); }}}
