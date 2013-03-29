package ca.ceaigp.muly.wavegraph.figures;

import org.eclipse.swt.graphics.Color;

import ca.ceaigp.muly.wavegraph.figures.Trace.TraceType;

public interface ITraceListener {

	void traceNameChanged(Trace trace, String oldName, String newName);

	void traceYAxisChanged(Trace trace, Axis oldName, Axis newName);

	void traceTypeChanged(Trace trace, TraceType old, TraceType newTraceType);

	void traceColorChanged(Trace trace, Color old, Color newColor);
}
