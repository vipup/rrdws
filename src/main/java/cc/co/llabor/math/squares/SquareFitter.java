package cc.co.llabor.math.squares;

public class SquareFitter {

	private double count;
	private double sumX;
	private double sumX2;
	private double sumXY;
	private double sumY;
	private double sumX3;
	private double sumX4;
	private double sumX2Y;

	public SquareFitter() {
		this.count = 0;
		this.sumX = 0;
		this.sumX2 = 0;
		this.sumX3 = 0;
		this.sumX4 = 0;
		this.sumY = 0;
		this.sumXY = 0;
		this.sumX2Y = 0;
	}

	SquareFitter add(int x, double y) {
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
		SquareFitter fitter = new SquareFitter(); 
	    for (int i = 0; i < data.length; i++)
	    {
	        fitter.add(i, data[i]);
	    }
	    return fitter.project(60);
	}

	public static void main(String[] args) {

		System.out.println("=== linearProject ===");
		double[] data = new double[] { 21410, 21886, 21837, 21895, 21564, 21714, 21571, 21324, 21310, 21390, 21764,
				21598, 21493, 21352, 21478, 21058, 20942, 20825, 21321, 20950, 21039, 21117, 20733, 20773, 20929, 20900,
				20687, 20999 };
		/*
		 * 
>>> squareProject([
        21410, 21886, 21837, 21895, 21564, 21714, 21571, 21324, 21310, 21390,
        21764, 21598, 21493, 21352, 21478, 21058, 20942, 20825, 21321, 20950,
        21039, 21117, 20733, 20773, 20929, 20900, 20687, 20999
    ], 60);
19282.85862700518
		 */
		System.out.println("= =" + squareProject(data ));

	}
}