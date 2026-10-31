package com.rs2.tools.mapgen;
import java.awt.*; import java.awt.image.BufferedImage; import java.util.*;
public class Preview {
 static int rgb(int c, double shade){ double h=((c>>10)&63)/64.0+0.0078125, s=((c>>7)&7)/8.0+0.0625, l=Math.min(1,(c&127)/128.0*shade);
  double q=l<0.5?l*(1+s):l+s-l*s, p=2*l-q; double[] o=new double[3]; double[] hs={h+1/3.0,h,h-1/3.0};
  for(int i=0;i<3;i++){double t=hs[i]; if(t<0)t+=1; if(t>1)t-=1; o[i]= t<1/6.0?p+(q-p)*6*t: t<0.5?q: t<2/3.0?p+(q-p)*(2/3.0-t)*6:p;}
  return new Color((float)Math.min(1,o[0]),(float)Math.min(1,o[1]),(float)Math.min(1,o[2])).getRGB(); }
 static void draw(Graphics2D g, ModelData m, int ox, int oy, double scale, double yaw, double pitch){
  double cy=Math.cos(yaw), sy=Math.sin(yaw), cp=Math.cos(pitch), sp=Math.sin(pitch);
  int n=m.vertices.size(); double[][] P=new double[n][3];
  for(int i=0;i<n;i++){int[] v=m.vertices.get(i); double x=v[0]*cy - v[2]*sy, z=v[0]*sy+v[2]*cy, y=v[1];
   double y2=y*cp - z*sp, z2=y*sp+z*cp; P[i]=new double[]{ox+x*scale, oy+y2*scale, z2};}
  Integer[] ord=new Integer[m.faces.size()]; for(int i=0;i<ord.length;i++)ord[i]=i;
  Arrays.sort(ord,(a,b)->{int[] fa=m.faces.get(a),fb=m.faces.get(b); double za=P[fa[0]][2]+P[fa[1]][2]+P[fa[2]][2], zb=P[fb[0]][2]+P[fb[1]][2]+P[fb[2]][2]; return Double.compare(zb,za);});
  double[] L={-0.5,-0.7,-0.5}; double ll=Math.sqrt(1.24); for(int i=0;i<3;i++)L[i]/=ll;
  for(int fi: ord){int[] f=m.faces.get(fi); double[] A=P[f[0]],B=P[f[1]],C=P[f[2]];
   double cross=(B[0]-A[0])*(C[1]-A[1])-(B[1]-A[1])*(C[0]-A[0]); if(cross>0) continue; // back face
   int[] a=m.vertices.get(f[0]),b=m.vertices.get(f[1]),c=m.vertices.get(f[2]);
   double ux=b[0]-a[0],uy=b[1]-a[1],uz=b[2]-a[2],vx=c[0]-a[0],vy=c[1]-a[1],vz=c[2]-a[2];
   double nx=uy*vz-uz*vy,ny=uz*vx-ux*vz,nz=ux*vy-uy*vx,nl=Math.sqrt(nx*nx+ny*ny+nz*nz)+1e-9;
   double d=(nx*L[0]+ny*L[1]+nz*L[2])/nl; double shade=0.75+0.45*d;
   g.setColor(new Color(rgb(m.colors.get(fi),shade)));
   Polygon p=new Polygon(new int[]{(int)A[0],(int)B[0],(int)C[0]},new int[]{(int)A[1],(int)B[1],(int)C[1]},3); g.fill(p); g.draw(p);}
 }
 public static void main(String[] a) throws Exception {
  try (CacheEditor c = new CacheEditor(java.nio.file.Paths.get(a[0]))) {
   ModelForge f = new ModelForge(c);
   BufferedImage img=new BufferedImage(1200,500,BufferedImage.TYPE_INT_RGB); Graphics2D g=img.createGraphics();
   g.setColor(new Color(60,70,60)); g.fillRect(0,0,1200,500);
   draw(g,f.swordInventory(),150,250,3.0,0.0,1.2); // seen from above, as in the inventory
   draw(g,f.swordWielded(),330,560,3.0,1.57,0.25);
   draw(g,f.read(518),330,700,3.0,1.57,0.25);
   draw(g,f.waystone(),560,420,1.4,0.6,0.3);
   draw(g,f.voidLantern(),800,420,1.4,0.5,0.3);
   draw(g,f.hollowedTree(),1030,420,1.4,0.4,0.3);
   javax.imageio.ImageIO.write(img,"png",new java.io.File(a[1]));
  }}}
