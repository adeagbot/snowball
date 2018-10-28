package main.java.app;

import main.scala.spark.AltmetricApp;
import main.scala.spark.BenchmarkApp;
import main.scala.spark.CafeApp;
import main.scala.spark.MendeleyApp;

public class Runner {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length!=1){
			System.err.println("Argument missing");
			System.exit(1);			
		}
		switch (args[0]){
			case "altmetric":AltmetricApp.main(args);
				break;
			case "mendeley": MendeleyApp.main(args);
				break;
			case "cafe":CafeApp.main(args);
				break;				
			case "benchmark":BenchmarkApp.main(args);
				break;
			default: 
				System.err.println("Args[0]:(altmetric|mendeley|cafe|benchmark)");
				System.exit(1);
		}
	};
}
