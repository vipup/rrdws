package cc.co.llabor.math.squares;

public class TrendFitter {

	private double count;
	private double sumX;
	private double sumX2;
	private double sumXY;
	private double sumY;
	private double sumX3;
	private double sumX4;
	private double sumX2Y;

	public TrendFitter() {
		this.count = 0;
		this.sumX = 0;
		this.sumX2 = 0;
		this.sumX3 = 0;
		this.sumX4 = 0;
		this.sumY = 0;
		this.sumXY = 0;
		this.sumX2Y = 0;
	}

	TrendFitter add(int x, double y) {
		this.count++;
		this.sumX += x;
		this.sumX2 += x * x;
		this.sumX3 += x * x * x;
		this.sumX4 += x * x * x * x;
		this.sumY += y;
		this.sumXY += x * y;
		this.sumX2Y += x * x * y;
		return this;
	}

	double project(double x) {
		double det = this.count*this.sumX2*this.sumX4 - this.count*this.sumX3*this.sumX3 - this.sumX*this.sumX*this.sumX4 + 2*this.sumX*this.sumX2*this.sumX3 - this.sumX2*this.sumX2*this.sumX2;
		double offset = this.sumX*this.sumX2Y*this.sumX3 - this.sumX*this.sumX4*this.sumXY - this.sumX2*this.sumX2*this.sumX2Y + this.sumX2*this.sumX3*this.sumXY + this.sumX2*this.sumX4*this.sumY - this.sumX3*this.sumX3*this.sumY;
		double scale = -this.count*this.sumX2Y*this.sumX3 + this.count*this.sumX4*this.sumXY + this.sumX*this.sumX2*this.sumX2Y - this.sumX*this.sumX4*this.sumY - this.sumX2*this.sumX2*this.sumXY + this.sumX2*this.sumX3*this.sumY;
		double accel = this.sumY*this.sumX*this.sumX3 - this.sumY*this.sumX2*this.sumX2 - this.sumXY*this.count*this.sumX3 + this.sumXY*this.sumX2*this.sumX - this.sumX2Y*this.sumX*this.sumX + this.sumX2Y*this.count*this.sumX2;
        return (offset + x*scale + x*x*accel)/det;
	}

	static double squareProject(double[] data ) {
		TrendFitter fitter = new TrendFitter(); 
	    for (int i = 0; i < data.length; i++)
	    {
	        fitter.add(i, data[i]);
	    }
	    return fitter.project(data.length+1);
	}

	public static void main(String[] args) {

		System.out.println("=== trendline ===");
		double[] data = new double[] {20,				32,				51,				43,				62,
								63,				82,				75,				92,				89};
		/* https://www.excel-easy.com/examples/trendline.html 		 */
		System.out.println(" trendline = " + squareProject(data));

	}
}