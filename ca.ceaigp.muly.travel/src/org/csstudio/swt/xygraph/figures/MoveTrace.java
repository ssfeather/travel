package org.csstudio.swt.xygraph.figures;


import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.swt.xygraph.undo.SaveStateCommand;
import org.csstudio.swt.xygraph.undo.ZoomCommand;
import org.csstudio.swt.xygraph.undo.ZoomType;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

public class MoveTrace extends Trace
{
	
	private ZoomType zoomType;
	private Cursor grabbing;
	private boolean armed;
	private Point start;
	private Point end;
	private XYGraph xyGraph;
	
	public MoveTrace(String name, Axis xAxis, Axis yAxis, IDataProvider dataProvider)
    {
		super(name, xAxis, yAxis, dataProvider);
	    // TODO Auto-generated constructor stub
		TracePlotMouseListener zoomer = new TracePlotMouseListener();
		addMouseListener(zoomer);
		addMouseMotionListener(zoomer);
		//grabbing = XYGraphMediaFactory.getCursor(CURSOR_TYPE.GRABBING);
		zoomType = ZoomType.NONE;
		
    }
	
	@Override
	public void setXYGraph(XYGraph xyGraph)
	{
		this.xyGraph = xyGraph;
	}
	
	
	private void zoomInOut(final boolean horizontally, final boolean vertically, final double factor)
	{
		if (horizontally) for (Axis axis : xyGraph.getXAxisList())
		{
			final double center = axis.getPositionValue(start.x, false);
			axis.zoomInOut(center, factor);
		}
		if (vertically) for (Axis axis : xyGraph.getYAxisList())
		{
			final double center = axis.getPositionValue(start.y, false);
			axis.zoomInOut(center, factor);
		}
	}
	
	public void setZoomType(final ZoomType zoomType)
	{
		this.zoomType = zoomType;
		setCursor(zoomType.getCursor());
	}
	
	/**
	 * Listener to mouse events, performs panning and some zooms Is very similar
	 * to the Axis.AxisMouseListener, but unclear how easy/useful it would be to
	 * base them on the same code.
	 */
	class TracePlotMouseListener extends MouseMotionListener.Stub implements MouseListener
	{
		final private List<Range> xAxisStartRangeList = new ArrayList<Range>();
		final private List<Range> yAxisStartRangeList = new ArrayList<Range>();
		private Range moveXRange;
		private Range moveYRange;

		private SaveStateCommand command;
		
		public void mousePressed(final MouseEvent me)
		{
			
			//System.out.println("**** MousePressed ****");
			//Only react to 'main' mouse button, only react to 'real' zoom
			//Enable ZoomType.NONE mouse event
			//
			//if (me.button != 1 || zoomType == ZoomType.NONE) return;
			if (me.button != 1) return;
			
			//-- Get Zoom Type
			zoomType = xyGraph.getZoomType();
			setZoomType(zoomType);
			
			armed = true;
			// get start position
			switch (zoomType)
			{
				case RUBBERBAND_ZOOM:  
					start = me.getLocation();
					end = null;
					break;
				case HORIZONTAL_ZOOM:
					start = new Point(me.getLocation().x, bounds.y);
					end = null;
					break;
				case VERTICAL_ZOOM:
					start = new Point(bounds.x, me.getLocation().y);
					end = null;
					break;
				case PANNING:
					System.out.println("**** MousePressed PANNING ****");
					setCursor(grabbing);
					start = me.getLocation();
					end = null;
					xAxisStartRangeList.clear();
					yAxisStartRangeList.clear();
					for (Axis axis : xyGraph.getXAxisList())
						xAxisStartRangeList.add(axis.getRange());
					for (Axis axis : xyGraph.getYAxisList())
						yAxisStartRangeList.add(axis.getRange());
					break;
				case NONE:
					System.out.println("**** MousePressed NONE ****");
					setCursor(grabbing);
					start = me.getLocation();
					end = null;
					/*
					xAxisStartRangeList.clear();
					yAxisStartRangeList.clear();
					for (Axis axis : xyGraph.getXAxisList())
						xAxisStartRangeList.add(axis.getRange());
					for (Axis axis : xyGraph.getYAxisList())
						yAxisStartRangeList.add(axis.getRange());
					*/
					moveXRange = getXAxis().getRange();
					moveYRange = getYAxis().getRange();
					break;
				case ZOOM_IN:
				case ZOOM_IN_HORIZONTALLY:
				case ZOOM_IN_VERTICALLY:
				case ZOOM_OUT:
				case ZOOM_OUT_HORIZONTALLY:
				case ZOOM_OUT_VERTICALLY:
					start = me.getLocation();
					end = new Point();
					// Start timer that will zoom while mouse button is pressed
					Display.getCurrent().timerExec(Axis.ZOOM_SPEED, new Runnable()
					{
						public void run()
						{
							if (!armed) return;
							performInOutZoom();
							Display.getCurrent().timerExec(Axis.ZOOM_SPEED, this);
						}
					});
					break;
				default:
					break;
			}

			// add command for undo operation
			command = new ZoomCommand(zoomType.getDescription(), xyGraph.getXAxisList(), xyGraph.getYAxisList());
			me.consume();
		}

		
		public void mouseDoubleClicked(final MouseEvent me)
		{ 
			System.out.println("**** MouseDoubleClicked ****");
		}

		@Override
		public void mouseDragged(final MouseEvent me)
		{
			//System.out.println("**** MouseDragged ****");
			if (!armed) return;
			switch (zoomType)
			{
				case RUBBERBAND_ZOOM:
					end = me.getLocation();
					break;
				case HORIZONTAL_ZOOM:
					end = new Point(me.getLocation().x, bounds.y + bounds.height);
					break;
				case VERTICAL_ZOOM:
					end = new Point(bounds.x + bounds.width, me.getLocation().y);
					break;
				case PANNING:
					//System.out.println("**** MouseDragged PANNING****");
					end = me.getLocation();
					pan();
					break;
				case NONE:
					System.out.println("**** MouseDragged NONE****");
					end = me.getLocation();
					movePan();
					break;
				default:
					break;
			}
			MoveTrace.this.repaint();
		}

		@Override
		public void mouseExited(final MouseEvent me)
		{
			//System.out.println("**** MouseExited ****");
			// Treat like releasing the button to stop zoomIn/Out timer
			switch (zoomType)
			{
				case ZOOM_IN:
				case ZOOM_IN_HORIZONTALLY:
				case ZOOM_IN_VERTICALLY:
				case ZOOM_OUT:
				case ZOOM_OUT_HORIZONTALLY:
				case ZOOM_OUT_VERTICALLY:
					mouseReleased(me);
				default:
			}
		}

		public void mouseReleased(final MouseEvent me)
		{
			System.out.println("**** MouseReleased ****");
			if (!armed) return;
			armed = false;
			if (zoomType == ZoomType.PANNING) setCursor(zoomType.getCursor());
			if (end == null || start == null) return;

			switch (zoomType)
			{
				case RUBBERBAND_ZOOM:
					for (Axis axis : xyGraph.getXAxisList())
					{
						final double t1 = axis.getPositionValue(start.x, false);
						final double t2 = axis.getPositionValue(end.x, false);
						axis.setRange(t1, t2, true);
					}
					for (Axis axis : xyGraph.getYAxisList())
					{
						final double t1 = axis.getPositionValue(start.y, false);
						final double t2 = axis.getPositionValue(end.y, false);
						axis.setRange(t1, t2, true);
					}
					break;
				case HORIZONTAL_ZOOM:
					for (Axis axis : xyGraph.getXAxisList())
					{
						final double t1 = axis.getPositionValue(start.x, false);
						final double t2 = axis.getPositionValue(end.x, false);
						axis.setRange(t1, t2, true);
					}
					break;
				case VERTICAL_ZOOM:
					for (Axis axis : xyGraph.getYAxisList())
					{
						final double t1 = axis.getPositionValue(start.y, false);
						final double t2 = axis.getPositionValue(end.y, false);
						axis.setRange(t1, t2, true);
					}
					break;
				case PANNING:
					pan();
					break;
				case NONE:
					movePan();
					break;
				case ZOOM_IN:
				case ZOOM_IN_HORIZONTALLY:
				case ZOOM_IN_VERTICALLY:
				case ZOOM_OUT:
				case ZOOM_OUT_HORIZONTALLY:
				case ZOOM_OUT_VERTICALLY:
					performInOutZoom();
					break;
				default:
					break;
			}

			if (zoomType != ZoomType.NONE && command != null)
			{
				command.saveState();
				xyGraph.getOperationsManager().addCommand(command);
				command = null;
			}
			start = null;
			end = null;
			MoveTrace.this.repaint();
		}
		
		private void pan()
		{
			
			List<Axis> axes = xyGraph.getXAxisList();
			for (int i = 0; i < axes.size(); ++i)
			{
				final Axis axis = axes.get(i);
				//axis.setDirty(false);
				axis.pan(xAxisStartRangeList.get(i), axis.getPositionValue(start.x, false), axis.getPositionValue(end.x, false));
			}
			
			axes = xyGraph.getYAxisList();
			for (int i = 0; i < axes.size(); ++i)
			{
				final Axis axis = axes.get(i);
				//axis.setDirty(false);
				axis.pan(yAxisStartRangeList.get(i), axis.getPositionValue(start.y, false), axis.getPositionValue(end.y, false));
			}
			
		}
		private void movePan()
		{
			
			Axis axisX = getXAxis();
			axisX.pan(moveXRange, axisX.getPositionValue(start.x, false), axisX.getPositionValue(end.x, false));
			
			Axis axisY = getYAxis();
			axisY.pan(moveYRange, axisY.getPositionValue(start.y, false), axisY.getPositionValue(end.y, false));
			
		}

		private void performInOutZoom()
		{
			switch (zoomType)
			{
				case ZOOM_IN:
					zoomInOut(true, true, Axis.ZOOM_RATIO);
					break;
				case ZOOM_IN_HORIZONTALLY:
					zoomInOut(true, false, Axis.ZOOM_RATIO);
					break;
				case ZOOM_IN_VERTICALLY:
					zoomInOut(false, true, Axis.ZOOM_RATIO);
					break;
				case ZOOM_OUT:
					zoomInOut(true, true, -Axis.ZOOM_RATIO);
					break;
				case ZOOM_OUT_HORIZONTALLY:
					zoomInOut(true, false, -Axis.ZOOM_RATIO);
					break;
				case ZOOM_OUT_VERTICALLY:
					zoomInOut(false, true, -Axis.ZOOM_RATIO);
					break;
				default: // NOP
			}
		}
	}
	
}

