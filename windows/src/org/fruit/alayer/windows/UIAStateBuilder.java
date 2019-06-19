/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/


/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.windows;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.fruit.Assert;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.StateBuilder;
import org.fruit.alayer.Tags;
import org.fruit.alayer.exceptions.StateBuildException;

public final class UIAStateBuilder implements StateBuilder {

	private static final long serialVersionUID = 796655140981849818L;
	final double timeOut; // seconds
	transient ExecutorService executor;
	transient long automationPointer, treeFilterConditionPointer, cacheRequestPointer;
	// begin by urueda
	boolean accessBridgeEnabled;
	String SUTProcesses; // regex
	// end by urueda

	public UIAStateBuilder(){ this(10/*seconds*/,false,"");	}

	public UIAStateBuilder(double timeOut, boolean accessBridgeEnabled, String SUTProcesses){ // seconds
		Assert.isTrue(timeOut > 0);
		this.timeOut = timeOut;
		initialize();
		// begin by urueda
		this.accessBridgeEnabled = accessBridgeEnabled;
		this.SUTProcesses = SUTProcesses;
		if (accessBridgeEnabled)
			new Thread(){ public void run(){ Windows.InitializeAccessBridge(); } }.start(); // based on ferpasri
		// end by urueda
		executor = Executors.newFixedThreadPool(1);
	}

	private void initialize(){

		Windows.CoInitializeEx(0, Windows.COINIT_MULTITHREADED);

		// create an automation cache
		automationPointer = Windows.CoCreateInstance(Windows.Get_CLSID_CUIAutomation_Ptr(), 0, Windows.CLSCTX_INPROC_SERVER, Windows.Get_IID_IUIAutomation_Ptr());
		cacheRequestPointer = Windows.IUIAutomation_CreateCacheRequest(automationPointer);

		// scope and filter settings
		// only retrieve the control view
		long firstConditionPointer = Windows.IUIAutomation_get_ControlViewCondition(automationPointer);
		// next we only want the elements that are on screen
		treeFilterConditionPointer = Windows.IUIAutomation_CreateAndCondition(automationPointer, Windows.IUIAutomation_CreatePropertyCondition(automationPointer, Windows.UIA_IsOffscreenPropertyId, false), firstConditionPointer);
		//Windows.IUnknown_Release(pFirstCondition);

		// add the filter and treescope to the cache. For the scope we want the uiaElement and all of its descendants.
		Windows.IUIAutomationCacheRequest_put_TreeFilter(cacheRequestPointer, treeFilterConditionPointer);
		Windows.IUIAutomationCacheRequest_put_TreeScope(cacheRequestPointer, Windows.TreeScope_Subtree);
		Windows.IUIAutomationCacheRequest_put_AutomationElementMode(cacheRequestPointer, Windows.AutomationElementMode_Full);

		// cache patterns
		Windows.IUIAutomationCacheRequest_AddPattern(cacheRequestPointer, Windows.UIA_WindowPatternId);
		Windows.IUIAutomationCacheRequest_AddPattern(cacheRequestPointer, Windows.UIA_ValuePatternId);

		// cache properties
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_NamePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_AcceleratorKeyPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_AccessKeyPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_AutomationIdPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_BoundingRectanglePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ClassNamePropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ClickablePointPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ControllerForPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ControlTypePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_CulturePropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_DescribedByPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_FlowsToPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_FrameworkIdPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_FullDescriptionPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_HasKeyboardFocusPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_HelpTextPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsContentElementPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsControlElementPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsDataValidForFormPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsEnabledPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsKeyboardFocusablePropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsOffscreenPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsPasswordPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsRequiredForFormPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ItemStatusPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ItemTypePropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_LabeledByPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_LocalizedControlTypePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_NativeWindowHandlePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ProviderDescriptionPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_OrientationPropertyId);
//		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_RuntimeIdPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsWindowPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ProcessIdPropertyId);

		//pattern availability properties
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsAnnotationPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsDockPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsDragPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsDropTargetPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsExpandCollapsePatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsGridItemPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsGridPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsInvokePatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsItemContainerPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsLegacyIAccessiblePatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsMultipleViewPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsObjectModelPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsRangeValuePatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsScrollItemPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsScrollPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsSelectionItemPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsSelectionPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsSpreadsheetPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsSpreadsheetItemPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsStylesPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsSynchronizedInputPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsTableItemPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsTablePatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsTextChildPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsTextPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsTextPattern2AvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsTogglePatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsTransformPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsTransformPattern2AvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsValuePatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsVirtualizedItemPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsWindowPatternAvailablePropertyId);

		// begin by urueda
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ScrollHorizontallyScrollablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ScrollVerticallyScrollablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ScrollHorizontalViewSizePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ScrollVerticalViewSizePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ScrollHorizontalScrollPercentPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ScrollVerticalScrollPercentPropertyId);
		// end by urueda

		// window role properties
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_WindowIsTopmostPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_WindowCanMaximizePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_WindowCanMinimizePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_WindowIsModalPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_WindowWindowInteractionStatePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_WindowWindowVisualStatePropertyId);

	}

	public void release(){
		if(automationPointer != 0){
			Windows.IUnknown_Release(treeFilterConditionPointer);
			Windows.IUnknown_Release(cacheRequestPointer);
			Windows.IUnknown_Release(automationPointer);
			Windows.CoUninitialize();
			automationPointer = 0;
			executor.shutdown();
		}
	}

	public void finalize(){ release(); }

	public UIAState apply(SUT system) throws StateBuildException {
		try {
			Future<UIAState> future = executor.submit(new StateFetcher(system, automationPointer, cacheRequestPointer,
																	   this.accessBridgeEnabled, this.SUTProcesses));
			return future.get((long)(timeOut * 1000.0), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new StateBuildException(e);
		} catch (ExecutionException e) {
			e.printStackTrace(); // make the exception traceable
			throw new StateBuildException(e);
		} catch (TimeoutException e) {
			//UIAState ret = new UIAState(uiaRoot);
			UIAState ret = new UIAState(StateFetcher.buildRoot(system)); // by urueda
			ret.set(Tags.Role, Roles.Process);
			ret.set(Tags.NotResponding, true);
			return ret;
		}
	}

	/*private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		ois.defaultReadObject();
		initialize();
		executor = Executors.newFixedThreadPool(1);
	}

	private void writeObject(ObjectOutputStream oos) throws IOException{
		oos.defaultWriteObject();
	}*/

}
