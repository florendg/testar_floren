/***************************************************************************************************
 *
 * Copyright (c) 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
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


package org.fruit.monkey;

import es.upv.staq.testar.EventHandler;
import es.upv.staq.testar.FlashFeedback;
import es.upv.staq.testar.IEventListener;
import es.upv.staq.testar.serialisation.LogSerialiser;
import org.fruit.alayer.devices.MouseButtons;
import java.util.HashSet;
import java.util.Set;

import static org.jnativehook.keyboard.NativeKeyEvent.VC_0;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_ALT;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_DOWN;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_LEFT;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_RIGHT;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_SHIFT;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_SPACE;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_UP;


public abstract class RuntimeControlsProtocol extends AbstractProtocol implements IEventListener {

    //TODO create a settings to turn off all the runtime controls (for "headless"-mode, for example in continuous integration)

    protected double delay = Double.MIN_VALUE;
    protected Object[] userEvent = null;
    protected boolean markParentWidget = false;
    protected boolean visualizationOn = false;

    public enum Modes{
        Spy,
        Record,
        Generate,
        Quit,
        View,
        Replay;
    }

    protected Modes mode;
    private Set<Integer> pressedKeyCodes = new HashSet<>();

    public EventHandler initializeEventHandler() {
    	return new EventHandler(this);
    }


    //TODO think how the modes should be implemented
    /**
     * Implement the SHIFT + ARROW-LEFT or SHIFT + ARROW-RIGHT toggling mode feature
     * Show the flashfeedback in the upperleft corner of the screen
     */
    private synchronized void nextMode() {
    	switch(mode){
    	case Record:
    		mode = Modes.Generate; break;
    	case Generate:
    		mode = Modes.Record; break;
    	default:
    		break;
    	}

    	// Add some logging
    	// Add the FlashFeedback about the mode you are in in the upper left corner.
    	String modeParamS = "";
    	if (mode == Modes.Record)
    		modeParamS = " (" + settings.get(ConfigTags.TimeToWaitAfterAction) + " wait time between actions)";

    	String modeNfo = "'" + mode + "' mode active." + modeParamS;
    	LogSerialiser.log(modeNfo + "\n", LogSerialiser.LogLevel.Info);
    	FlashFeedback.flash(modeNfo, 1000);

    }

    /**
     * Set the mode with the given parameter value
     * @param mode
     */
    protected synchronized void setMode(Modes mode){
        if (mode() == mode) return;
        else this.mode = mode;
//        List<Modes> modesList = Arrays.asList(Modes.values());
//        while (mode() != mode)
//            nextMode(modesList.indexOf(mode) > modesList.indexOf(mode()));
    }


    /**
     * Return the mode TESTAR is currently in
     * @return
     */
    public synchronized Modes mode(){ return mode; }

    private final static double SLOW_MOTION = 2.0;
    //TODO: key commands come through java.awt.event but are the key codes same for all OS? if they are the same, then move to platform independent protocol?
    //TODO: Investigate better shortcut combinations to control TESTAR that does not interfere with SUT
    // (e.g. SHIFT + 1 puts an ! in the notepad and hence interferes with SUT state, but the
    // event is not recorded as a user event).
    /**
     * Override the default keylistener to implement the TESTAR shortcuts
     * SHIFT + SPACE
     * SHIFT + ARROW-UP
     * SHIFT + ARROW-RIGHT
     * SHIFT + ARROW-LEFT
     * SHIFT + ARROW-DOWN
     * SHIFT + {0, 1, 2, 3, 4}
     * SHIFT + ALT
     * @param keyCode
     */
    @Override
    public void keyDown(int keyCode) {
        pressedKeyCodes.add(keyCode);
        //  SHIFT + SPACE are pressed --> Toggle slow motion test
        if (pressedKeyCodes.contains(VC_SHIFT) && keyCode == VC_SPACE) {
            if (this.delay == Double.MIN_VALUE) {
                this.delay = settings().get(ConfigTags.TimeToWaitAfterAction).doubleValue();
                settings().set(ConfigTags.TimeToWaitAfterAction, SLOW_MOTION);
            } else{
                settings().set(ConfigTags.TimeToWaitAfterAction, this.delay);
                delay = Double.MIN_VALUE;
            }
        }

            // SHIFT + ARROW-RIGHT --> go to the next mode
        else if(keyCode == VC_RIGHT && pressedKeyCodes.contains(VC_SHIFT)) {
            if(mode.equals(Modes.Record) || mode.equals(Modes.Generate))
            	nextMode();
        }

            // SHIFT + ARROW-LEFT --> go to the previous mode
        else if(keyCode == VC_LEFT && pressedKeyCodes.contains(VC_SHIFT)) {
            if(mode.equals(Modes.Record) || mode.equals(Modes.Generate))
            	nextMode();
        }

            // SHIFT + ARROW-DOWN --> stop TESTAR run
        else if(keyCode == VC_DOWN && pressedKeyCodes.contains(VC_SHIFT)) {
            LogSerialiser.log("User requested to stop monkey!\n", LogSerialiser.LogLevel.Info);
            mode = Modes.Quit;
        }

        // SHIFT + ARROW-UP --> toggle visualization on / off
        else if(keyCode == VC_UP && pressedKeyCodes.contains(VC_SHIFT)) {
            if(visualizationOn){
                visualizationOn = false;
            }else{
                visualizationOn = true;
            }
        }

        //Disabled and replaced with Shift + Arrow Up to toggle visualization on/off:
//        // SHIFT + 1 --> toggle action visualization
//        else if(key == KBKeys.VK_1 && pressed.contains(KBKeys.VK_SHIFT))
//            settings().set(ConfigTags.VisualizeActions, !settings().get(ConfigTags.VisualizeActions));
//
//            // SHIFT + 2 --> toggle showing accessibility properties of the widget
//        else if(key == KBKeys.VK_2 && pressed.contains(KBKeys.VK_SHIFT))
//            settings().set(ConfigTags.DrawWidgetUnderCursor, !settings().get(ConfigTags.DrawWidgetUnderCursor));
//
//            // SHIFT + 3 --> toggle basic or all accessibility properties of the widget
//        else if(key == KBKeys.VK_3 && pressed.contains(KBKeys.VK_SHIFT))
//            settings().set(ConfigTags.DrawWidgetInfo, !settings().get(ConfigTags.DrawWidgetInfo));
//
//            // SHIFT + 4 --> toggle the widget tree
//        else if (key == KBKeys.VK_4  && pressed.contains(KBKeys.VK_SHIFT))
//            settings().set(ConfigTags.DrawWidgetTree, !settings.get(ConfigTags.DrawWidgetTree));

            // SHIFT + 0 --> undocumented feature
        else if (keyCode == VC_0 && pressedKeyCodes.contains(VC_SHIFT))
            System.setProperty("DEBUG_WINDOWS_PROCESS_NAMES","true");

            // TODO: Find out if this commented code is anything useful
		/*else if (key == KBKeys.VK_ENTER && pressed.contains(KBKeys.VK_SHIFT)){
			AdhocServer.startAdhocServer();
			mode = Modes.AdhocTest;
			LogSerialiser.log("'" + mode + "' mode active.\n", LogSerialiser.LogLevel.Info);
		}*/

            // In GenerateManual mode you can press any key except SHIFT to add a user keyboard
            // This is because SHIFT is used for the TESTAR shortcuts
            // This is not ideal, because now special characters and capital letters and other events that needs SHIFT
            // cannot be recorded as an user event in GenerateManual....
        else if (!pressedKeyCodes.contains(VC_SHIFT) && mode() == Modes.Record && userEvent == null) {
            //System.out.println("USER_EVENT key_down! " + key.toString());
            userEvent = new Object[]{keyCode}; // would be ideal to set it up at keyUp
        }

        // SHIFT + ALT --> Toggle widget-tree hieracrhy display
        if (pressedKeyCodes.contains(VC_ALT) && pressedKeyCodes.contains(VC_SHIFT))
            markParentWidget = !markParentWidget;
    }

    //jnativehook is platform independent
    @Override
    public void keyUp(int key){
        pressedKeyCodes.remove(key);
    }

    /**
     * TESTAR does not listen to mouse down clicks in any mode
     * @param btn
     * @param x
     * @param y
     */
    @Override
    public void mouseDown(MouseButtons btn, double x, double y){}

    /**
     * In GenerateManual the user can add user events by clicking and the ecent is added when releasing the mouse
     * @param btn
     * @param x
     * @param y
     */
    @Override
    public void mouseUp(MouseButtons btn, double x, double y){
        // In GenerateManual the user can add user events by clicking
        if (mode() == Modes.Record && userEvent == null){
            //System.out.println("USER_EVENT mouse_up!");
            userEvent = new Object[]{
                    btn,
                    new Double(x),
                    new Double(y)
            };
        }
    }
}
