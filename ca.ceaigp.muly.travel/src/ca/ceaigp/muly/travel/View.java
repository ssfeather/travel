package ca.ceaigp.muly.travel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


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
		XYGraph xyGraph = new XYGraph();

		ToolbarArmedXYGraph toolbarArmedXYGraph = new ToolbarArmedXYGraph(xyGraph);

		xyGraph.setTitle("Seismic Wave");
		// set it as the content of LightwightSystem
		lws.setContents(toolbarArmedXYGraph);
		
		xyGraph.setFont(XYGraphMediaFactory.getInstance().getFont(XYGraphMediaFactory.FONT_TAHOMA));
		xyGraph.primaryXAxis.setTitle("Time");
		xyGraph.primaryYAxis.setTitle("Amplitude");
		xyGraph.primaryXAxis.setRange(new Range(0,200));
		xyGraph.primaryXAxis.setDateEnabled(true);
		xyGraph.primaryYAxis.setAutoScale(true);
		xyGraph.primaryXAxis.setAutoScale(true);
		xyGraph.primaryXAxis.setShowMajorGrid(true);
		xyGraph.primaryYAxis.setShowMajorGrid(true);
		xyGraph.primaryXAxis.setAutoScaleThreshold(0);
		//------------------------------------------------------------
		final Axis x2Axis = new Axis("X-2", false);
		x2Axis.setTickLableSide(LabelSide.Secondary);
		//x2Axis.setAutoScale(true);
		xyGraph.addAxis(x2Axis);


		final Axis y2Axis = new Axis("Log Scale", true);
		y2Axis.setRange(10, 500);
		y2Axis.setLogScale(true);
		//y2Axis.setAutoScale(true);
		y2Axis.setForegroundColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_PINK));
		y2Axis.setTickLableSide(LabelSide.Secondary);
		xyGraph.addAxis(y2Axis);
		
		Axis y3Axis = new Axis("Y-3", true);
		y3Axis.setForegroundColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_BLUE));
		y3Axis.setTickLableSide(LabelSide.Secondary);
		y3Axis.setRange(new Range(-2, 3));
		y3Axis.setShowMajorGrid(false);
		y3Axis.setAutoScale(true);
//		xyGraph.addAxis(y3Axis);
		
		CircularBufferDataProvider trace2Provider = new CircularBufferDataProvider(true);
		trace2Provider.setBufferSize(100);
		trace2Provider.setUpdateDelay(100);
		//-------------------------------------------------------------------------

		// create a trace data provider, which will provide the data to the
		// trace.
		SacTimeSeries sac = getSacData("/Users/macuser/SeisData/test1.sac");
		
		CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(true);
		
		float[] sacx = sac.getX();
		float[] sacy = sac.getY();
		traceDataProvider.setBufferSize(sacy.length);
		//traceDataProvider.setUpdateDelay(100);
		traceDataProvider.setCurrentXDataArray(sacx);
		traceDataProvider.setCurrentYDataArray(sacy);
		xyGraph.primaryXAxis.setAutoScale(true);
		xyGraph.primaryYAxis.setAutoScale(true);
		
		// create the trace
		Trace trace = new Trace("Wave1", xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider);

		// set trace property
		// trace.setPointStyle(PointStyle.XCROSS);
		//trace.setPointStyle(PointStyle.FILLED_DIAMOND);
		trace.setPointStyle(PointStyle.NONE);
		
		//xyGraph.primaryXAxis.getAutoScaleThreshold();
		//xyGraph.primaryYAxis.getAutoScaleThreshold();
		
		// add the trace to xyGraph
		xyGraph.addTrace(trace);
		
		//--------------------------------------------------------------------------------------------------------
		SacTimeSeries sac1 = getSacData("/Users/macuser/SeisData/test3.sac");
		
		CircularBufferDataProvider traceDataProvider1 = new CircularBufferDataProvider(true);
		float[] sacx1 = sac1.getX();
		float[] sacy1 = sac1.getY();
		traceDataProvider1.setBufferSize(sacy1.length);
		traceDataProvider1.setCurrentXDataArray(sacx1);
		traceDataProvider1.setCurrentYDataArray(sacy1);
		//xyGraph.primaryXAxis.setAutoScale(true);
		//xyGraph.primaryYAxis.setAutoScale(true);
		
		// create the trace
		Axis ax = new Axis("AX",false);
		Axis ay = new Axis("AY",true);
		
		ax.setAutoScale(true);
		ay.setAutoScale(true);
		//ax.setRange(0, sacx1.length);
		//ay.setRange(null);
		Trace trace1 = new Trace("Wave2",xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider1);
		
		// set trace property
		// trace.setPointStyle(PointStyle.XCROSS);
		//trace.setPointStyle(PointStyle.FILLED_DIAMOND);
		trace1.setPointStyle(PointStyle.NONE);
		
		//xyGraph.primaryXAxis.getAutoScaleThreshold();
		//xyGraph.primaryYAxis.getAutoScaleThreshold();
		
		// add the trace to xyGraph
		xyGraph.addTrace(trace1);
		
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