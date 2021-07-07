/**
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2021 Universitat Politecnica de Valencia - www.upv.es
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
 *
 */

package org.fruit.alayer.webdriver;

import es.upv.staq.testar.ProtocolUtil;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import org.fruit.alayer.Shape;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.actions.WdActionRoles;
import org.fruit.alayer.*;
import org.fruit.alayer.webdriver.enums.WdTags;

import java.awt.*;
import java.util.List;

import static org.fruit.alayer.webdriver.Constants.scrollThick;


public class WdProtocolUtil extends ProtocolUtil {

  public static String getStateshot(State state) {
    double width = CanvasDimensions.getCanvasWidth() + (
        state.get(WdTags.WebVerticallyScrollable) ? scrollThick : 0);
    double height = CanvasDimensions.getCanvasHeight() + (
        state.get(WdTags.WebHorizontallyScrollable) ? scrollThick : 0);
    Rect rect = Rect.from(0, 0, width, height);
    AWTCanvas screenshot = WdScreenshot.fromScreenshot(rect, state.get(Tags.HWND, (long)0));
    return ScreenshotSerialiser.saveStateshot(state.get(Tags.ConcreteIDCustom), screenshot);
  }

  public static String getActionshot(State state, Action action) {
    if(action.get(Tags.Role, ActionRoles.Action).isA(WdActionRoles.RemoteAction)) {
        return getRemoteActionshot(state, action);
    }
    
    List<Finder> targets = action.get(Tags.Targets, null);
    if (targets == null) {
      return null;
    }

    Rectangle actionArea = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    for (Finder f : targets) {
      Widget widget = f.apply(state);
      Shape shape = widget.get(Tags.Shape);
      Rectangle r = new Rectangle((int) shape.x(), (int) shape.y(), (int) shape.width(), (int) shape.height());
      actionArea = actionArea.union(r);
    }
    if (actionArea.isEmpty()) {
      return null;
    }

    // Actionarea is outside viewport
    if (actionArea.x < 0 || actionArea.y < 0 ||
        actionArea.x + actionArea.width > CanvasDimensions.getCanvasWidth() ||
        actionArea.y + actionArea.height > CanvasDimensions.getCanvasHeight()) {
      return null;
    }

    Rect rect = Rect.from(actionArea.x, actionArea.y, actionArea.width + 1, actionArea.height + 1);
    AWTCanvas scrshot = WdScreenshot.fromScreenshot(rect, state.get(Tags.HWND, (long)0));
    return ScreenshotSerialiser.saveActionshot(state.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable"), action.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable"), scrshot);
  }
  
  private static String getRemoteActionshot(State state, Action action) {
      Rectangle actionArea = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
      Shape shape = action.get(Tags.OriginWidget).get(Tags.Shape);
      Rectangle r = new Rectangle((int) shape.x(), (int) shape.y(), (int) shape.width(), (int) shape.height());
      actionArea = actionArea.union(r);

      if (actionArea.isEmpty()) {
          return null;
      }

      // Actionarea is outside viewport
      if (actionArea.x < 0 || actionArea.y < 0 ||
              actionArea.x + actionArea.width > CanvasDimensions.getCanvasWidth() ||
              actionArea.y + actionArea.height > CanvasDimensions.getCanvasHeight()) {
          return null;
      }

      Rect rect = Rect.from(actionArea.x, actionArea.y, actionArea.width + 1, actionArea.height + 1);
      AWTCanvas scrshot = WdScreenshot.fromScreenshot(rect, state.get(Tags.HWND, (long)0));
      return ScreenshotSerialiser.saveActionshot(state.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable"), action.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable"), scrshot);
  }
  
  public static AWTCanvas getStateshotBinary(State state) {
	  //If these State Tags are not obtained, the State has an error, use full monitor screen
	  if(state.get(WdTags.WebVerticallyScrollable, null) == null 
			  && state.get(WdTags.WebHorizontallyScrollable, null) == null) {
		  //Get a screenshot of all the screen, because SUT ended and we can't obtain the size
		  Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		  AWTCanvas scrshot = AWTCanvas.fromScreenshot(Rect.from(screenRect.getX(), screenRect.getY(),
				  screenRect.getWidth(), screenRect.getHeight()), state.get(Tags.HWND, (long)0), AWTCanvas.StorageFormat.PNG, 1);
		  return scrshot;
	  }
	  
	  double width = CanvasDimensions.getCanvasWidth() + (
			  state.get(WdTags.WebVerticallyScrollable) ? scrollThick : 0);
	  double height = CanvasDimensions.getCanvasHeight() + (
			  state.get(WdTags.WebHorizontallyScrollable) ? scrollThick : 0);
	  Rect rect = Rect.from(0, 0, width, height);
	  AWTCanvas screenshot = WdScreenshot.fromScreenshot(rect, state.get(Tags.HWND, (long)0));
	  return screenshot;
  }
}