package ca.ceaigp.muly.travel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;

import edu.sc.seis.TauP.MatTauP_Curve;
import edu.sc.seis.TauP.TT_Curve;

public class DrawCurve
{
	public DrawCurve(String[] travelArgs, XYGraph swtFigure)
    {
		// Taup Draw Curve
				String[] curveArgs = new String[6];
				curveArgs[0] = "-mod";
				curveArgs[1] = travelArgs[0];
				curveArgs[2] = "-h";
				curveArgs[3] = travelArgs[1];
				curveArgs[4] = "-ph";
				curveArgs[5] = travelArgs[2];
				
				TT_Curve[] ttcurve = null;
				try
				{
					ttcurve = MatTauP_Curve.run_curve(curveArgs);
				}
				catch (OptionalDataException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (StreamCorruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (TT_Curve ttc : ttcurve)
				{
					float[] dists = new float[ttc.dist.length];
					float[] times = new float[ttc.time.length];

					double[] tempfd = ttc.dist;
					for (int i = 0; i < ttc.dist.length; i++)
					{
						Double fd = new Double(tempfd[i]);
						dists[i] = fd.floatValue();
					}
					tempfd = ttc.time;
					for (int i = 0; i < ttc.time.length; i++)
					{
						Double fd = new Double(tempfd[i]);
						times[i] = fd.floatValue();
						// System.out.println("Time: " + times[i]);
					}

					CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
					traceDataProvider.setBufferSize(9000);
					traceDataProvider.setCurrentXDataArray(times);
					traceDataProvider.setCurrentYDataArray(dists);

					Trace trace1 = new Trace(ttc.phaseName + "_" + ttc.sourceDepth, swtFigure.primaryXAxis, swtFigure.primaryYAxis, traceDataProvider);
					swtFigure.addTrace(trace1);
				}
    }
}
