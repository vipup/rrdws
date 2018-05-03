package cc.co.llabor.math.squares;

public class LineFitter {

	private double count;
	private double sumX;
	private double sumX2;
	private double sumXY;
	private double sumY; 

	public LineFitter() {
		this.count = 0;
		this.sumX = 0;
		this.sumX2 = 0;
		this.sumXY = 0;
		this.sumY = 0;
	}
  
	LineFitter add(int x, double y) {
			this.count++;
	        this.sumX += x;
	        this.sumX2 += x*x;
	        this.sumXY += x*y;
	        this.sumY += y;
	        return this;
	}
	 
	double project (double x)
	    {
		double det = this.count * this.sumX2 - this.sumX * this.sumX;
	        double offset = (this.sumX2 * this.sumY - this.sumX * this.sumXY) / det;
	        double scale = (this.count * this.sumXY - this.sumX * this.sumY) / det;
	        return offset + x * scale;
	    }
 
	static double linearProject(double [] data, double x)
	{
		LineFitter fitter = new LineFitter();
	    for (int i = 0; i < data.length; i++)
	    {
	        fitter.add(i, data[i]);
	    }
	    return fitter.project(x);
	}

	public static void main(String[] args) {

		System.out.println("=== linearProject ===");
		double[] data = new double[] {
              21410, 21886, 21837, 21895, 21564, 21714, 21571, 21324, 21310, 21390,
              21764, 21598, 21493, 21352, 21478, 21058, 20942, 20825, 21321, 20950,
              21039, 21117, 20733, 20773, 20929, 20900, 20687, 20999
		};
		/*
 * 
You can do a least-squares fit of a line. 
 
Example:

>>> linearProject([
        21410, 21886, 21837, 21895, 21564, 21714, 21571, 21324, 21310, 21390,
        21764, 21598, 21493, 21352, 21478, 21058, 20942, 20825, 21321, 20950,
        21039, 21117, 20733, 20773, 20929, 20900, 20687, 20999
    ], 60);
19489.614121510676
 */
		System.out.println("= ="+linearProject (data , 60) );

	}
}