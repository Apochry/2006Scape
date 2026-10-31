package com.rs2.tools.mapgen;
import java.awt.*; import java.awt.image.BufferedImage; import java.util.*; import java.util.List;
/** Contact-sheet helper around (a copy of) Preview's software renderer: auto-fits each model into a labelled cell. */
public class Sheet {
 final int cw, ch, cols; final List<Object[]> cells = new ArrayList<>();
 Sheet(int cw, int ch, int cols){this.cw=cw;this.ch=ch;this.cols=cols;}
 /** yaw/pitch in radians, Preview conventions (yaw 0 = looking at the player's front, from -z). */
 void add(String label, ModelData m, double yaw, double pitch){cells.add(new Object[]{label,m,yaw,pitch,null});}
 void add(String label, ModelData m, double yaw, double pitch, double fixedScale){cells.add(new Object[]{label,m,yaw,pitch,fixedScale});}
 static double[] project(ModelData m,double yaw,double pitch){ double cy=Math.cos(yaw), sy=Math.sin(yaw), cp=Math.cos(pitch), sp=Math.sin(pitch);
  double[] b={1e9,1e9,-1e9,-1e9}; for(int[] v:m.vertices){double x=v[0]*cy - v[2]*sy, z=v[0]*sy+v[2]*cy, y=v[1]; double y2=y*cp - z*sp;
   b[0]=Math.min(b[0],x);b[1]=Math.min(b[1],y2);b[2]=Math.max(b[2],x);b[3]=Math.max(b[3],y2);} return b;}
 void write(String path) throws Exception {
  int rows=(cells.size()+cols-1)/cols; BufferedImage img=new BufferedImage(cw*cols,ch*rows,BufferedImage.TYPE_INT_RGB); Graphics2D g=img.createGraphics();
  g.setColor(new Color(62,53,41)); g.fillRect(0,0,img.getWidth(),img.getHeight());
  for(int i=0;i<cells.size();i++){Object[] c=cells.get(i); int cx=(i%cols)*cw, cyy=(i/cols)*ch; ModelData m=(ModelData)c[1]; double yaw=(Double)c[2], pitch=(Double)c[3];
   g.setColor(new Color(72,63,50)); g.fillRect(cx+2,cyy+2,cw-4,ch-4);
   double[] b=project(m,yaw,pitch); double s=c[4]!=null?(Double)c[4]:Math.min((cw-24)/(b[2]-b[0]+1),(ch-40)/(b[3]-b[1]+1));
   int ox=(int)(cx+cw/2-(b[0]+b[2])/2*s), oy=(int)(cyy+(ch-16)/2+8-(b[1]+b[3])/2*s);
   Shape clip=g.getClip(); g.setClip(cx+2,cyy+2,cw-4,ch-4); Preview.draw(g,m,ox,oy,s,yaw,pitch); g.setClip(clip);
   g.setColor(new Color(255,152,31)); g.setFont(new Font("Dialog",Font.BOLD,12)); g.drawString((String)c[0],cx+6,cyy+ch-8);}
  javax.imageio.ImageIO.write(img,"png",new java.io.File(path)); }
 static ModelData recolour(ModelData m, int... pairs){ for(int i=0;i<m.colors.size();i++) for(int k=0;k<pairs.length;k+=2) if(m.colors.get(i)==pairs[k]){m.colors.set(i,pairs[k+1]);break;} return m;}
}
