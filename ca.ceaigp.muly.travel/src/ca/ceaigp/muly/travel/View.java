package ca.ceaigp.muly.travel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.sc.seis.TauP.MatTauP_Curve;
import edu.sc.seis.TauP.TT_Curve;
import edu.sc.seis.seisFile.sac.SacTimeSeries;


public class View extends ViewPart
{
	public static final String ID = "ca.ceaigp.muly.travel.view";

	private LightweightSystem lws;

	/**
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */
	class ViewContentProvider implements IStructuredContentProvider
	{
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object parent)
		{
			if (parent instanceof Object[])
			{
				return (Object[]) parent;
			}
			return new Object[0];
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public String getColumnText(Object obj, int index)
		{
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index)
		{
			return getImage(obj);
		}

		public Image getImage(Object obj)
		{
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	
	private SacTimeSeries getSacData(String fn)
	{
		SacTimeSeries sac = new SacTimeSeries();
		try
        {
	       sac.read(new File(fn));
        }
        catch (FileNotFoundException e)
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
        catch (IOException e)
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		return sac;
	}
	
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		// use LightweightSystem to create the bridge between SWT and draw2D
		lws = new LightweightSystem(new Canvas(parent, SWT.NONE));

		// create a new XY Graph.
		//XYGraph xyGraph = new XYGraph();
		XYGraph swtFigure = new XYGraph();
		
		ToolbarArmedXYGraph toolbarArmedXYGraph = new ToolbarArmedXYGraph(swtFigure);

		swtFigure.setTitle("Seismic Wave");
		// set it as the content of LightwightSystem
		lws.setContents(toolbarArmedXYGraph);
		
		swtFigure.setFont(XYGraphMediaFactory.getInstance().getFont(XYGraphMediaFactory.FONT_TAHOMA));
		swtFigure.primaryXAxis.setTitle("Time");
		swtFigure.primaryYAxis.setTitle("Dist");
		//swtFigure.primaryXAxis.setRange(new Range(0,200));
		//swtFigure.primaryYAxis.setRange(new Range(-200,200));
		//swtFigure.primaryXAxis.setDateEnabled(true);
		swtFigure.primaryYAxis.setAutoScale(true);
		swtFigure.primaryXAxis.setAutoScale(true);
		swtFigure.primaryXAxis.setShowMajorGrid(true);
		swtFigure.primaryYAxis.setShowMajorGrid(true);
		//swtFigure.primaryXAxis.setAutoFormat(true);
		//swtFigure.getPlotArea().setShowBorder(true);
		//�����᲻�ɼ�
		//swtFigure.primaryXAxis.setVisible(false);
		//swtFigure.primaryYAxis.setVisible(false);
		
		//--------------------------------------------------------------------------------------------------------
		// Taup Draw Curve
		String[] travelArgs = new String[3];
		travelArgs[0] = "iasp91";
		travelArgs[1] = "62";
		travelArgs[2] = "p, s, P, S, Pn, Sn, PcP, ScS";
		new DrawCurve(travelArgs, swtFigure);
		
		travelArgs[0] = "iasp91";
		travelArgs[1] = "10";
		travelArgs[2] = "P,pP,S,sS";
		new DrawCurve(travelArgs, swtFigure);
/*
		TT_Curve[] ttcurve  = null;
		try
		{
			String[] curveArgs = new String[6];
			curveArgs[0] = "-mod";
			curveArgs[1] = "iasp91";
			curveArgs[2] = "-h"; 
			curveArgs[3] = "62";
			curveArgs[4] = "-ph";
			curveArgs[5] = "p, s, P, S, Pn, Sn, PcP, ScS";
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
		
		for(TT_Curve ttc : ttcurve)
		{
			float[] dists = new float[ttc.dist.length];
			float[] times = new float[ttc.time.length];
			
			double[] tempfd = ttc.dist;
			for(int i=0; i<ttc.dist.length; i++)
			{	
				Double fd = new Double(tempfd[i]);
				dists[i] = fd.floatValue();
			}
			tempfd = ttc.time;
			for(int i=0; i<ttc.time.length; i++)
			{	
				Double fd = new Double(tempfd[i]);
				times[i] = fd.floatValue();
				//System.out.println("Time: " + times[i]);
			}
			
			CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
			traceDataProvider.setBufferSize(9000);
			traceDataProvider.setCurrentXDataArray(times);
			traceDataProvider.setCurrentYDataArray(dists);
		
			Trace trace1 = new Trace(ttc.phaseName + "_" + ttc.sourceDepth ,swtFigure.primaryXAxis, swtFigure.primaryYAxis, traceDataProvider);
			swtFigure.addTrace(trace1);
		}
*/	
		//--------------------------------------------------------------------------------------------------------
/*		
		SacTimeSeries sac1 = getSacData("/Users/macuser/SeisData/test2.sac");
		CircularBufferDataProvider traceDataProvider1 = new CircularBufferDataProvider(true);
		float[] sacx1 = sac1.getX();
		float[] sacy1 = sac1.getY();
		traceDataProvider1.setBufferSize(sacy1.length);
		traceDataProvider1.setCurrentXDataArray(sacx1);
		traceDataProvider1.setCurrentYDataArray(sacy1);
	
		Trace trace1 = new Trace("Wave1",swtFigure.primaryXAxis, swtFigure.primaryYAxis, traceDataProvider1);
		swtFigure.addTrace(trace1);
*/		
		//-----------------------------------------------------------------------------------------------------
		
		SacTimeSeries sac2 = getSacData("/Users/macuser/SeisData/test2.sac");
		float[] sacx2 = sac2.getX();
		float[] sacy2 = sac2.getY();
		
		//System.out.println("Range X: " + (swtFigure.primaryXAxis.getRange().getUpper() - swtFigure.primaryXAxis.getRange().getLower()));
		//System.out.println("Data Length: " + sac2.getHeader().getDelta() + " ; " + sac2.getNumPtsRead() + " ; " + sac2.getNumPtsRead()*sac2.getHeader().getDelta());
		//double dataTimeScale = sac2.getNumPtsRead()*sac2.getHeader().getDelta()/(swtFigure.primaryXAxis.getRange().getUpper() - swtFigure.primaryXAxis.getRange().getLower());
		//System.out.println("Data Time scale: " + dataTimeScale);
		
		CircularBufferDataProvider traceDataProvider2 = new CircularBufferDataProvider(true);
		
		traceDataProvider2.setBufferSize(sacy2.length);
		traceDataProvider2.setCurrentXDataArray(sacx2);
		traceDataProvider2.setCurrentYDataArray(sacy2);
		//traceDataProvider2.addSample(sample);
		//traceDataProvider2.getSample(1);
		
		//-------------------------------------------------------------------------------------------------------
		Axis x2Axis = new Axis("X2", false);
		Axis y2Axis = new Axis("Y2", true);
		
		x2Axis.setTickLableSide(LabelSide.Secondary);
		y2Axis.setTickLableSide(LabelSide.Secondary);

		x2Axis.setTitle("Time");
		y2Axis.setTitle("Amplitude");
		//x2Axis.setRange(new Range(0,200));
		//y2Axis.setRange(new Range(-200,200));
		//x2Axis.setDateEnabled(true);
		
		y2Axis.setAutoScale(true);
		//x2Axis.setAutoScale(true);
		
		x2Axis.setRange(new Range(swtFigure.primaryXAxis.getRange().getLower(), swtFigure.primaryXAxis.getRange().getUpper()/sac2.getHeader().getDelta()));
		//x2Axis.setRange(swtFigure.primaryXAxis.getRange());
		y2Axis.setRange(swtFigure.primaryYAxis.getRange());
		
		//x2Axis.setShowMajorGrid(true);
		//y2Axis.setShowMajorGrid(true);
		x2Axis.setAutoScaleThreshold(0);
		swtFigure.addAxis(x2Axis);
		swtFigure.addAxis(y2Axis);
		
		//x2Axis.setVisible(false);
		//y2Axis.setVisible(false);
		
		//------------------------------------------------------------------------------------------------------------------------

		//MoveTrace trace2 = new MoveTrace("Wave2",swtFigure.primaryXAxis, swtFigure.primaryYAxis, traceDataProvider2);
		//Trace trace2 = new Trace("Wave2",swtFigure.primaryXAxis, swtFigure.primaryYAxis, traceDataProvider2, true);
		
		//MoveTrace trace2 = new MoveTrace("Wave2",x2Axis, y2Axis, traceDataProvider2);
		//trace2.setXYGraph(swtFigure);
		Trace trace2 = new Trace("Wave",x2Axis, y2Axis, traceDataProvider2);
		trace2.setEnableMove(true);
		trace2.setTraceColor(new Color(null, new RGB(0,0,255)));
		
		swtFigure.addTrace(trace2);
		
		/*
		//Test log4j     
        Properties prop = new Properties();
        try
        {
	        prop.load(Activator.class.getResourceAsStream("log4j.properties"));
        }
        catch (IOException e)
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
        PropertyConfigurator.configure(prop);
		Logger logger = Logger.getLogger(View.class);
		logger.log(Level.WARN, "This is a test...");
		*/
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		
	}
}