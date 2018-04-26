package nl.ou.testar.tgherkin.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;


/**
 * Tgherkin DoubleClickGesture.
 *
 */
public class DoubleClickGesture extends Gesture {

    /**
     * DoubleClickGesture constructor.
     * @param arguments list of arguments
     */
    public DoubleClickGesture(List<Argument> arguments) {
    	super(arguments);
    }
	
    
    @Override
    public boolean gesturePossible(Widget widget, ActionWidgetProxy proxy, DataTable dataTable) {
    	if (getArguments().size() > 0 && getBooleanArgument(0, dataTable)) {    		 
    		// unchecked argument contains value true
    		return super.gesturePossible(widget, proxy, dataTable);
    	}    	
    	return proxy.isClickable(widget);
    }
    
    @Override
    public Set<Action> getActions(Widget widget, ActionWidgetProxy proxy, DataTable dataTable) {
		Set<Action> actions = new HashSet<Action>();	
    	StdActionCompiler ac = new AnnotatingActionCompiler();
    	actions.add(ac.leftDoubleClickAt(widget));
    	return actions;
    }
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
   		result.append("doubleClick");
   		result.append(argumentsToString());
    	return result.toString();    	
    }
}