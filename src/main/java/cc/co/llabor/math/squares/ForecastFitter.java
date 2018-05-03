package cc.co.llabor.math.squares;

public class ForecastFitter {

	private double count;
	private double sumX;
	private double sumX2;
	private double sumXY;
	private double sumY; 

	public ForecastFitter() {
		this.count = 0;
		this.sumX = 0;
		this.sumX2 = 0;
		this.sumXY = 0;
		this.sumY = 0;
	}
  
	ForecastFitter add(int x, double y) {
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
		ForecastFitter fitter = new ForecastFitter();
	    for (int i = 0; i < data.length; i++)
	    {
	        fitter.add(i, data[i]);
	    }
	    return fitter.project(x);
	}

	public static void main(String[] args) {

		System.out.println("=== forecast ===");
		double[] data = new double[] {20,				32,				51,				43,				62,
				63,				82,				75,				92,				89
		};
		/*  https://www.excel-easy.com/examples/forecast-trend.html */
		System.out.println("forecast-trend ="+linearProject (data , 11) );

	}
}