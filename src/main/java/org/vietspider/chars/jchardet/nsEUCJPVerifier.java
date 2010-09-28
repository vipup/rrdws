package org.vietspider.chars.jchardet;



public class nsEUCJPVerifier extends nsVerifier {

	 static int[]  cclass   ; 
	 static int[]  states   ; 
	 static int    stFactor ; 
	 static String charset  ; 

	 public int[]  cclass()   { return cclass ;   }
	 public int[]  states()   { return states ;   }
	 public int    stFactor() { return stFactor ; }
	 public String charset()  { return charset ;  }

   public nsEUCJPVerifier() {

      cclass = new int[256/8] ;

      cclass[0] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[1] = ((int)(((  ((int)(((  ((int)((( 5) << 4) | (5)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[2] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[3] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((5) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[4] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[5] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[6] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[7] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[8] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[9] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[10] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[11] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[12] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[13] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[14] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[15] = ((int)(((  ((int)(((  ((int)((( 4) << 4) | (4)))  ) << 8) | (((int)(((4) << 4) | ( 4))) ))) ) << 16) | (  ((int)(((  ((int)(((4) << 4) | (4))) ) << 8) | (   ((int)(((4) << 4) | (4))) )))))) ;
      cclass[16] = ((int)(((  ((int)(((  ((int)((( 5) << 4) | (5)))  ) << 8) | (((int)(((5) << 4) | ( 5))) ))) ) << 16) | (  ((int)(((  ((int)(((5) << 4) | (5))) ) << 8) | (   ((int)(((5) << 4) | (5))) )))))) ;
      cclass[17] = ((int)(((  ((int)(((  ((int)((( 3) << 4) | (1)))  ) << 8) | (((int)(((5) << 4) | ( 5))) ))) ) << 16) | (  ((int)(((  ((int)(((5) << 4) | (5))) ) << 8) | (   ((int)(((5) << 4) | (5))) )))))) ;
      cclass[18] = ((int)(((  ((int)(((  ((int)((( 5) << 4) | (5)))  ) << 8) | (((int)(((5) << 4) | ( 5))) ))) ) << 16) | (  ((int)(((  ((int)(((5) << 4) | (5))) ) << 8) | (   ((int)(((5) << 4) | (5))) )))))) ;
      cclass[19] = ((int)(((  ((int)(((  ((int)((( 5) << 4) | (5)))  ) << 8) | (((int)(((5) << 4) | ( 5))) ))) ) << 16) | (  ((int)(((  ((int)(((5) << 4) | (5))) ) << 8) | (   ((int)(((5) << 4) | (5))) )))))) ;
      cclass[20] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (5))) )))))) ;
      cclass[21] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[22] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[23] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[24] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[25] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[26] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[27] = ((int)(((  ((int)(((  ((int)((( 2) << 4) | (2)))  ) << 8) | (((int)(((2) << 4) | ( 2))) ))) ) << 16) | (  ((int)(((  ((int)(((2) << 4) | (2))) ) << 8) | (   ((int)(((2) << 4) | (2))) )))))) ;
      cclass[28] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[29] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[30] = ((int)(((  ((int)(((  ((int)((( 0) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;
      cclass[31] = ((int)(((  ((int)(((  ((int)((( 5) << 4) | (0)))  ) << 8) | (((int)(((0) << 4) | ( 0))) ))) ) << 16) | (  ((int)(((  ((int)(((0) << 4) | (0))) ) << 8) | (   ((int)(((0) << 4) | (0))) )))))) ;



      states = new int[5] ;

      states[0] = ((int)(((  ((int)(((  ((int)((( eError) << 4) | (eError)))  ) << 8) | (((int)(((eError) << 4) | ( eStart))) ))) ) << 16) | (  ((int)(((  ((int)(((     5) << 4) | (     3))) ) << 8) | (   ((int)(((     4) << 4) | (     3))) )))))) ;
      states[1] = ((int)(((  ((int)(((  ((int)((( eItsMe) << 4) | (eItsMe)))  ) << 8) | (((int)(((eItsMe) << 4) | ( eItsMe))) ))) ) << 16) | (  ((int)(((  ((int)(((eError) << 4) | (eError))) ) << 8) | (   ((int)(((eError) << 4) | (eError))) )))))) ;
      states[2] = ((int)(((  ((int)(((  ((int)((( eError) << 4) | (eError)))  ) << 8) | (((int)(((eError) << 4) | ( eStart))) ))) ) << 16) | (  ((int)(((  ((int)(((eError) << 4) | (eStart))) ) << 8) | (   ((int)(((eItsMe) << 4) | (eItsMe))) )))))) ;
      states[3] = ((int)(((  ((int)(((  ((int)((( eError) << 4) | (     3)))  ) << 8) | (((int)(((eError) << 4) | ( eError))) ))) ) << 16) | (  ((int)(((  ((int)(((eError) << 4) | (eStart))) ) << 8) | (   ((int)(((eError) << 4) | (eError))) )))))) ;
      states[4] = ((int)(((  ((int)(((  ((int)((( eStart) << 4) | (eStart)))  ) << 8) | (((int)(((eStart) << 4) | ( eStart))) ))) ) << 16) | (  ((int)(((  ((int)(((eError) << 4) | (eError))) ) << 8) | (   ((int)(((eError) << 4) | (     3))) )))))) ;



      charset =  "EUC-JP";
      stFactor =  6;

   }

   public boolean isUCS2() { return  false; } ;


}
