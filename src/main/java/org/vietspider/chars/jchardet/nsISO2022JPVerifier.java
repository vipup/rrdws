package org.vietspider.chars.jchardet;



public class nsISO2022JPVerifier extends nsVerifier {

	 static int[]  cclass   ; 
	 static int[]  states   ; 
	 static int    stFactor ; 
	 static String charset  ; 

	 public int[]  cclass()   { return cclass ;   }
	 public int[]  states()   { return states ;   }
	 public int    stFactor() { return stFactor ; }
	 public String charset()  { return charset ;  }

   public nsISO2022JPVerifier() {

      cclass = new int[256/8] ;

      cclass[0] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (2))) )))))) ;
      cclass[1] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[2] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[3] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((1) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[4] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 7))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[5] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (3))) )))))) ;
      cclass[6] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[7] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[8] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (4))) ) << 8) | (   ((int)(((0) << 4) | (6))) )))))) ;
      cclass[9] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (5))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[10] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[11] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[12] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[13] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[14] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[15] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[16] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[17] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[18] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[19] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[20] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[21] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[22] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[23] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[24] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[25] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[26] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[27] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[28] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[29] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[30] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[31] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;



      states = new int[6] ;

      states[0] = ((int)(((  ((int)(((  ((int)((( eStart) << 4) | (eStart)))  ) << 8) | (((int)(((eStart) << 4) | ( eStart))) ))) ) << 16) | (  ((int)(((  ((int)(((eStart) << 4) | (eError))) ) << 8) | (   ((int)(((     3) << 4) | (eStart))) )))))) ;
      states[1] = ((int)(((  ((int)(((  ((int)((( eError) << 4) | (eError)))  ) << 8) | (((int)(((eError) << 4) | ( eError))) ))) ) << 16) | (  ((int)(((  ((int)(((eError) << 4) | (eError))) ) << 8) | (   ((int)(((eError) << 4) | (eError))) )))))) ;
      states[2] = ((int)(((  ((int)(((  ((int)((( eItsMe) << 4) | (eItsMe)))  ) << 8) | (((int)(((eItsMe) << 4) | ( eItsMe))) ))) ) << 16) | (  ((int)(((  ((int)(((eItsMe) << 4) | (eItsMe))) ) << 8) | (   ((int)(((eItsMe) << 4) | (eItsMe))) )))))) ;
      states[3] = ((int)(((  ((int)(((  ((int)(((      4) << 4) | (eError)))  ) << 8) | (((int)(((eError) << 4) | ( eError))) ))) ) << 16) | (  ((int)(((  ((int)(((     5) << 4) | (eError))) ) << 8) | (   ((int)(((eError) << 4) | (eError))) )))))) ;
      states[4] = ((int)(((  ((int)(((  ((int)((( eError) << 4) | (eItsMe)))  ) << 8) | (((int)(((eError) << 4) | ( eItsMe))) ))) ) << 16) | (  ((int)(((  ((int)(((eError) << 4) | (eError))) ) << 8) | (   ((int)(((eError) << 4) | (eError))) )))))) ;
      states[5] = ((int)(((  ((int)(((  ((int)((( eError) << 4) | (eError)))  ) << 8) | (((int)(((eItsMe) << 4) | ( eItsMe))) ))) ) << 16) | (  ((int)(((  ((int)(((eError) << 4) | (eError))) ) << 8) | (   ((int)(((eError) << 4) | (eError))) )))))) ;



      charset =  "ISO-2022-JP";
      stFactor =  8;

   }

   public boolean isUCS2() { return  false; } ;


}
