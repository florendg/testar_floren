/***************************************************************************************************
*
* Copyright (c) 2013 - 2020 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 - 2020 Open Universiteit - www.ou.nl
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
package org.fruit.alayer.actions;

import org.fruit.alayer.Role;

public final class ActionRoles {
	private ActionRoles(){}

	public static final Role

	Action = Role.from("Action"),
	MouseAction = Role.from("MouseAction", Action),
	KeyboardAction = Role.from("KeyboardAction", Action),
	CompoundAction = Role.from("CompoundAction", Action),
	MouseMove = Role.from("MouseMove", MouseAction),
	MouseDown = Role.from("MouseDown", MouseAction),
	KeyDown = Role.from("KeyDown", KeyboardAction),
	MouseUp = Role.from("MouseUp", MouseAction),
	KeyUp = Role.from("KeyUp", KeyboardAction),
	HitKey = Role.from("HitKey", KeyDown, KeyUp),
	HitShortcutKey = Role.from("HitShortcutKey", KeyDown, KeyUp),
	Click = Role.from("Click", MouseDown, MouseUp),
	LeftClick = Role.from("LeftClick", Click),
	RightClick = Role.from("RightClick", Click),
	DoubleClick = Role.from("DoubleClick", Click),
	LDoubleClick = Role.from("LDoubleClick", LeftClick, DoubleClick),
	RDoubleClick = Role.from("RDoubleClick", RightClick, DoubleClick),
	ClickAt = Role.from("ClickAt", Click, MouseMove),
	LeftClickAt = Role.from("LeftClickAt", ClickAt, LeftClick),
	RightClickAt = Role.from("RightClickAt", ClickAt, RightClick),
	DoubleClickAt = Role.from("DoubleClickAt", ClickAt, DoubleClick),
	LDoubleClickAt = Role.from("LDoubleClickAt", DoubleClickAt, LeftClick),
	RDoubleClickAt = Role.from("RDoubleClickAt", DoubleClickAt, RightClick),
	Type = Role.from("Type", HitKey),
	Paste = Role.from("Paste", HitKey),
	ClickTypeInto = Role.from("ClickTypeInto", ClickAt, Type),
	DropDown = Role.from("DropDown", Click, KeyDown),
	PasteTextInto = Role.from("PasteTextInto", ClickAt, Paste),
	Drag = Role.from("Drag", MouseDown, MouseUp, MouseMove),
	LeftDrag = Role.from("LeftDrag", Drag);
}
