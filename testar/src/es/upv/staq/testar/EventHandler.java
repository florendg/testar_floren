/***************************************************************************************************
*
* Copyright (c) 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2019 Open Universiteit - www.ou.nl
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


package es.upv.staq.testar;

import org.fruit.alayer.devices.MouseButtons;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;

/**
 * An event handler for the testing protocol layers.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */

public class EventHandler implements NativeKeyListener, NativeMouseListener, NativeMouseMotionListener {

	private IEventListener eventListener;
	
	public EventHandler(IEventListener eventListener){
		this.eventListener = eventListener;
	}
	
	// ##############################################
	//  AbstractProtocol layer handlers (refactored)
	// ##############################################

	@Override
	public final void nativeKeyPressed (final NativeKeyEvent key) {
		eventListener.keyDown(key.getKeyCode());
    }

	@Override
	public final void nativeKeyReleased (NativeKeyEvent key) {
		eventListener.keyUp(key.getKeyCode());
    }

	@Override
	public final void nativeMouseClicked(NativeMouseEvent arg0) {}

	@Override
	public final void nativeMousePressed(NativeMouseEvent arg0) {
		if(arg0.getButton() == 1)
			eventListener.mouseDown(MouseButtons.BUTTON1, arg0.getX(), arg0.getY());
		else if(arg0.getButton() == 2)
			eventListener.mouseDown(MouseButtons.BUTTON3, arg0.getX(), arg0.getY());
		else if(arg0.getButton() == 3)
			eventListener.mouseDown(MouseButtons.BUTTON2, arg0.getX(), arg0.getY());
	}

	@Override
	public final void nativeMouseReleased(NativeMouseEvent arg0) {
		if(arg0.getButton() == 1)
			eventListener.mouseUp(MouseButtons.BUTTON1, arg0.getX(), arg0.getY());
		else if(arg0.getButton() == 2)
			eventListener.mouseUp(MouseButtons.BUTTON3, arg0.getX(), arg0.getY());
		else if(arg0.getButton() == 3)
			eventListener.mouseUp(MouseButtons.BUTTON2, arg0.getX(), arg0.getY());
	}

	// #########################################
	//  ClickFilterLayerProtocol event handlers
	// #########################################
	
	@Override
	public void nativeMouseMoved(NativeMouseEvent e) {
		eventListener.mouseMoved(e.getX(),e.getY());
	}
	
	@Override
	public void nativeMouseDragged(NativeMouseEvent arg0) {}
	
	@Override
	public final void nativeKeyTyped(NativeKeyEvent e) {}
        
}
