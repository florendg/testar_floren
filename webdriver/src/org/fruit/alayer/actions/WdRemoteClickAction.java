/**
 * Copyright (c) 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2021 Universitat Politecnica de Valencia - www.upv.es
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

package org.fruit.alayer.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.Assert;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.PositionException;
import org.fruit.alayer.webdriver.WdWidget;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

public class WdRemoteClickAction extends TaggableBase implements Action {

    protected WdWidget widget;
    protected static final Logger logger = LogManager.getLogger();
    
    private static final Pen LClickPen = Pen.newPen().setColor(Color.Green)
            .setFillPattern(FillPattern.Solid).setStrokeWidth(3).build();

    public static class ClickVisualizer implements Visualizer {
        private static final long serialVersionUID = -2006402344810634504L;
        
        private final double width, height;
        private final Pen pen;
        private final Rect rec;

        public ClickVisualizer(Rect rec, Pen pen, double width, double height){
            Assert.notNull(rec, pen);
            this.width = width;
            this.height = height;
            this.pen = pen;
            this.rec = rec;
        }

        public void run(State state, Canvas canvas, Pen pen) {
            Assert.notNull(state, canvas, pen);
            pen = Pen.merge(pen, this.pen);
            try {
                double mx = rec.x() + (rec.width() / 2.0);
                double my = rec.y() + (rec.height() / 2.0);
                canvas.ellipse(pen, mx - width * .5, my - height * .5, width, height);
            } catch (PositionException pe) {}
        }
    }

    public WdRemoteClickAction(WdWidget widget) {
        this.widget = widget;
        this.set(Tags.OriginWidget, widget);
        this.set(Tags.Desc, "Remote click " + widget.element.remoteWebElement.getId());
        this.set(Tags.Role, WdActionRoles.RemoteClick);
        Role role = widget.get(Tags.Role, Roles.Widget);
        if (!role.equals(WdRoles.WdOPTION)) {
            this.set(Tags.Visualizer, new ClickVisualizer(widget.element.rect, LClickPen, 10, 10));
        }
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        try {
            RemoteWebElement remoteElement = widget.element.remoteWebElement;
            remoteElement.click();
        }
        catch (Exception e) {
            logger.warn("Remote click action failed", e);
        }
    }

    @Override
    public String toShortString() {
        return "Remote click " + widget.element.getElementDescription();
    }

    @Override
    public String toParametersString() {
        return toShortString();
    }

    @Override
    public String toString(Role... discardParameters) {
        return toShortString();
    }
}
