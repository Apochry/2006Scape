package com.rs2.tools.mapgen;
import java.nio.ByteBuffer; import java.util.*;
/** Finds items by name and prints their model opcodes; also dumps identity kits (idk). */
public class ArmourDump { public static void main(String[] a) throws Exception {
 try (CacheEditor c = new CacheEditor(java.nio.file.Paths.get(a[0]))) {
  CacheEditor.Archive cfg = CacheEditor.Archive.decode(c.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
  ConfigDefinitions.Table t = ConfigDefinitions.load(cfg, "obj", false);
  for (int id = 0; id < t.size(); id++) { ConfigDefinitions.Definition d = t.decode(id); String n = d.text(2);
   if (n == null) continue; boolean hit=false; for (int i=1;i<a.length;i++) if (n.equalsIgnoreCase(a[i])) hit=true; if(!hit) continue;
   StringBuilder sb = new StringBuilder(id+" "+n+" :");
   for (int op : new int[]{1,4,5,6,7,8,23,24,25,26,78,79,90,91,92,93,40}) { byte[] p = d.get(op); if (p==null) continue;
    sb.append(" op"+op+"="); if (op==40){int k=p[0]&255; for(int i=0;i<k;i++) sb.append(((p[1+i*4]&255)<<8|(p[2+i*4]&255))+">"+((p[3+i*4]&255)<<8|(p[4+i*4]&255))+",");}
    else { sb.append((p[0]&255)<<8|(p[1]&255)); if (p.length==3) sb.append("/off"+p[2]); } }
   System.out.println(sb); }
  byte[] idk = cfg.get("idk.dat"); ByteBuffer b = ByteBuffer.wrap(idk); int n = b.getShort()&0xFFFF; System.out.println("idk count "+n);
  for (int i=0;i<n;i++){ int part=-1; boolean nonSel=false; List<Integer> models=new ArrayList<>(), heads=new ArrayList<>(); int[] rc=new int[20];
   while(true){int op=b.get()&255; if(op==0)break; if(op==1)part=b.get(); else if(op==2){int k=b.get()&255; for(int j=0;j<k;j++)models.add(b.getShort()&0xFFFF);} else if(op==3)nonSel=true;
    else if(op>=40&&op<50) rc[op-40]=b.getShort()&0xFFFF; else if(op>=50&&op<60) rc[op-50+10]=b.getShort()&0xFFFF; else if(op>=60&&op<70) heads.add(b.getShort()&0xFFFF); else System.out.println("?op"+op);}
   System.out.println("kit "+i+" part="+part+(nonSel?" nonsel":"")+" models="+models+" heads="+heads); }
 }}}
