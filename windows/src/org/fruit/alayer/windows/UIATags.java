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

import org.fruit.alayer.Rect;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TagsBase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class UIATags extends TagsBase {
	
	private UIATags() {}


	/////////////// GENERAL PROPERTIES ///////////

	/**
	 * Type of a widget in localized form (language)
	 * From the UIA documentation:
	 *
	 * Identifies the LocalizedControlType property, which is a text string describing the type of control that the automation element represents. The string should contain only lowercase characters:
	 * Correct: "button"
	 * Incorrect: "Button"
	 *
	 * When LocalizedControlType is not specified by the element provider, the default localized string is supplied by the framework, according to the control type of the element (for example, "button" for the Button control type). An automation element with the Custom control type must support a localized control type string that represents the role of the element (for example, "color picker" for a custom control that enables users to choose and specify colors).
	 * When a custom value is supplied, the string must match the application UI language or the operating system default UI language.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIALocalizedControlType = from("UIALocalizedControlType", String.class);
	
	/**
	 * UIA Type name of a widget. From the UIA documentation:
	 *
	 * Identifies the ItemType property, which is a text string describing the type of the automation element.
	 * ItemType is used to obtain information about items in a list, tree view, or data grid. For example, an item in a file directory view might be a "Document File" or a "Folder".
	 * When ItemType is supported, the string must match the application UI language or the operating system default UI language.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAItemType = from("UIAItemType", String.class);

	/**
	 * From the UIA documentation:
	 *
	 * Identifies the ItemStatus property, which is a text string describing the status of an item of the automation element.
	 * ItemStatus enables a client to ascertain whether an element is conveying status about an item as well as what the status is. For example, an item associated with a contact in a messaging application might be "Busy" or "Connected".
	 * When ItemStatus is supported, the string must match the application UI language or the operating system default UI language.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAItemStatus = from("UIAItemStatus", String.class);

	/**
	 * From the UIA documentation:
	 *
	 * Identifies the ProviderDescription property, which is a formatted string containing the source information of the UI Automation provider for the automation element, including proxy information.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAProviderDescription = from("UIAProviderDescription", String.class);

	/**
	 * The full description for a widget/UIAelement. From the UIA documentation:
	 *
	 * The FullDescription property exposes a localized string which can contain extended description text for an element. FullDescription can contain a more complete description of an element than may be appropriate for the element Name.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAFullDescription = from("UIAFullDescription", String.class);

	/**
	 * Win32 API ClassName of a widget/UIAelement. From the UIA documentation:
	 *
	 * Identifies the ClassName property, which is a string containing the class name for the automation element as assigned by the control developer.
	 * The class name depends on the implementation of the UI Automation provider and therefore is not always in a standard format. However, if the class name is known, it can be used to verify that an application is working with the expected automation element.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAClassName = from("UIAClassName", String.class);
	
	/**
	 * Help text of a widget. From the UIA documentation:
	 *
	 * Identifies the HelpText property, which is a help text string associated with the automation element.
	 * The HelpText property can be supported with placeholder text appearing in edit or list controls. For example, "Type text here for search" is a good candidate the HelpText property for an edit control that places the text prior to the user's actual input. However, it is not adequate for the name property of the edit control.
	 * When HelpText is supported, the string must match the application UI language or the operating system default UI language.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAHelpText = from("UIAHelpText", String.class);
	
	/**
	 * Title or name of a widget.
	 * From the UIA documentation:
	 *
	 * Identifies the Name property, which is a string that holds the name of the automation element.
	 * The Name property should be the same as the label text on screen. For example, Name should be "Browse" for a button element with the label "Browse". The Name property must not include the mnemonic character for the access keys (that is, "&"), which is underlined in the UI text presentation. Also, the Name property should not be an extended or modified version of the on-screen label because the inconsistency between the name and the label can cause confusion among client applications and users.
	 * When the corresponding label text is not visible on screen, or when it is replaced by graphics, alternative text should be chosen. The alternative text should be concise, intuitive, and localized to the application UI language, or to the operating system default UI language. The alternative text should not be a detailed description of the visual details, but a concise description of the UI function or feature as if it were labeled by simple text. For example, the Windows Start menu button is named "Start" (button) instead of "Windows Logo on blue round sphere graphics" (button). For more information, see Creating Text Equivalents for Images.
	 * When a UI label uses text graphics (for example, using ">>" for a button that adds an item from left to right), the Name property should be overridden by an appropriate text alternative (for example, "Add"). However the practice of using text graphics as a UI label is discouraged due to both localization and accessibility concerns.
	 * The Name property must not include the control role or type information, such as "button" or "list"; otherwise, it will conflict with the text from the LocalizedControlType property when these two properties are appended (many existing assistive technologies do this).
	 * The Name property cannot be used as a unique identifier among siblings. However, as long as it is consistent with the UI presentation, the same Name value can be supported among peers. For test automation, the clients should consider using the AutomationId or RuntimeId property.
	 * Text controls do not always have to have the Name property be identical to the text that is displayed within the control, so long as the Text pattern is also supported.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAName = from("UIAName", String.class);
	
	/**
	 * UIA Control Type Identifier of a widget/UIAelement. From the UIA documentation:
	 *
	 * Identifies the ControlType property, which is a class that identifies the type of the automation element. ControlType defines characteristics of the UI elements by well known UI control primitives such as button or check box.
	 * Variant type: VT_I4
	 * Default value: UIA_CustomControlTypeId
	 * [!Note]
	 * Use the default value only if the automation element represents a completely new type of control.
	 */
	public static final Tag<Long> UIAControlType = from("UIAControlType", Long.class);
	
	/**
	 * UIA Culture Identifier of a widget/UIAelement. From the UIA documentation:
	 *
	 * Identifies the Culture property, which contains a locale identifier for the automation element (for example, 0x0409 for "en-US" or English (United States)).
	 * Each locale has a unique identifier, a 32-bit value that consists of a language identifier and a sort order identifier. The locale identifier is a standard international numeric abbreviation and has the components necessary to uniquely identify one of the installed operating system-defined locales. For more information, see Language Identifier Constants and Strings.
	 * This property may exist on a per-control basis, but typically is only available on an application level.
	 * Variant type: VT_I4
	 * Default value: 0
	 */
	public static final Tag<Long> UIACulture = from("UIACulture", Long.class);
	
	/**
	 * If the widget is a Win32 widget, this will be its native handle
	 *
	 * From the UIA documentation:
	 *
	 * Identifies the NativeWindowHandle property, which is an integer that represents the handle (HWND) of the automation element window, if it exists; otherwise, this property is 0.
	 * Variant type: VT_I4
	 * Default value: 0
	 */
	public static final Tag<Long> UIANativeWindowHandle = from("UIANativeWindowHandle", Long.class);

	/**
	 * Identifies the orientation of the UIAelement/widget.
	 * From the UIA documentation:
	 *
	 * Identifies the Orientation property, which indicates the orientation of the control represented by the automation element. The property is expressed as a value from the OrientationType enumerated type.
	 * The Orientation property is supported by controls, such as scroll bars and sliders, that can have either a vertical or a horizontal orientation. Otherwise, it can always be OrientationType_None, which means that the control has no orientation.
	 * Variant type: VT_I4
	 * Default value: 0 (OrientationType_None)
	 */
	public static final Tag<Long> UIAOrientation = from("UIAOrientation", Long.class);
	

	
	/**
	 * Id of the process that this widget belongs to
	 * From the UIA documentation:
	 *
	 * Identifies the ProcessId property, which is an integer representing the process identifier (ID) of the automation element.
	 * The process identifier (ID) is assigned by the operating system. It can be seen in the PID column of the Processes tab in Task Manager.
	 * Variant type: VT_I4
	 * Default value: 0
	 */
	public static final Tag<Long> UIAProcessId = from("UIAProcessId", Long.class);
	
	/**
	 * Id of the framework that this widget/UIAelement belongs to (e.g. Win32, Swing, Flash, ...)
	 * From the UIA documentation:
	 *
	 * Identifies the FrameworkId property, which is a string containing the name of the underlying UI framework that the automation element belongs to.
	 * The FrameworkId enables client applications to process automation elements differently depending on the particular UI framework. Examples of property values include "Win32", "WinForm", and "DirectUI".
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAFrameworkId = from("UIAFrameworkId", String.class);

	/**
	 * UIA automation id of a UIA element. From the UIA documentation:
	 *
	 * Identifies the AutomationId property, which is a string containing the UI Automation identifier (ID) for the automation element.
	 * When it is available, the AutomationId of an element must be the same in any instance of the application, regardless of the local language. The value should be unique among sibling elements, but not necessarily unique across the entire desktop. For example, multiple instances of an application, or multiple folder views in Microsoft Windows Explorer, can contain elements with the same AutomationId property, such as "SystemMenuBar".
	 * Although support for AutomationId is always recommended for better automated testing support, this property is not mandatory. Where it is supported, AutomationId is useful for creating a test automation script that runs regardless of the UI language. Clients should make no assumptions regarding the AutomationId values exposed by other applications. AutomationId is not guaranteed to be stable across different releases or builds of an application.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAAutomationId = from("UIAAutomationId", String.class);

	/**
	 * From the UIA documentation:
	 *
	 * Identifies the RuntimeId property, which is an array of integers representing the identifier for an automation element.
	 * The identifier is unique on the desktop, but it is guaranteed to be unique only within the UI of the desktop on which it was generated. Identifiers can be reused over time.
	 * The format of RuntimeId can change. The returned identifier should be treated as an opaque value and used only for comparison; for example, to determine whether an automation element is in the cache.
	 * Variant type: VT_I4 | VT_ARRAY
	 * Default value: VT_EMPTY
	 */
	public static final Tag<long[]> UIARuntimeId = from("UIARuntimeId", long[].class);
	
	/**
	 * Whether a widget is a content uiaElement. From the UIA documentation:
	 *
	 * Identifies the IsContentElement property, which is a Boolean value that specifies whether the element appears in the content view of the automation element tree. For more information, see UI Automation Tree Overview.
	 * [!Note]
	 * For an element to appear in the content view, both the IsContentElement property and the IsControlElement property must be TRUE.
	 *
	 *
	 * Variant type: VT_BOOL
	 * Default value: TRUE
	 */
	public static final Tag<Boolean> UIAIsContentElement = from("UIAIsContentElement", Boolean.class);
	
	/**
	 * Whether a widget is a control uiaElement. From the UIA documentation:
	 *
	 * Identifies the IsControlElement property, which is a Boolean value that specifies whether the element appears in the control view of the automation element tree. For more information, see UI Automation Tree Overview.
	 * Variant type: VT_BOOL
	 * Default value: TRUE
	 */
	public static final Tag<Boolean> UIAIsControlElement = from("UIAIsControlElement", Boolean.class);
	
	/**
	 * If the widget is a window, this tells whether it is the top-most window
	 */
	public static final Tag<Boolean> UIAIsTopmostWindow = from("UIAIsTopmostWindow", Boolean.class);

	/**
	 * If the widget is a window, this tells if it is modal (such as a message box)
	 */
	public static final Tag<Boolean> UIAIsWindowModal = from("UIAIsWindowModal", Boolean.class);
	public static final Tag<Long> UIAWindowInteractionState = from("UIAWindowInteractionState", Long.class);
	public static final Tag<Long> UIAWindowVisualState = from("UIAWindowVisualState", Long.class);
	
	/**
	 * Bounding rectangle of a widget/UIAelement. From the UIA documentation:
	 * Identifies the BoundingRectangle property, which specifies the coordinates of the rectangle that completely encloses the automation element. The rectangle is expressed in physical screen coordinates. It can contain points that are not clickable if the shape or clickable region of the UI item is irregular, or if the item is obscured by other UI elements.
	 * Variant type: VT_R8 | VT_ARRAY
	 * Default value: [0,0,0,0]
	 * [!Note]
	 * This property is NULL if the item is not currently displaying a UI.
	 */
	public static final Tag<Rect> UIABoundingRectangle = from("UIABoundingRectangle", Rect.class);
	
	/**
	 * Whether this widget is currently enabled or disabled (e.g. "greyed out")
	 * From the UIA documentation:
	 *
	 * Identifies the IsEnabled property, which is a Boolean value that indicates whether the UI item referenced by the automation element is enabled and can be interacted with.
	 * When the enabled state of a control is FALSE, it is assumed that child controls are also not enabled. Clients should not expect property-changed events from child elements when the state of the parent control changes.
	 * Variant type: VT_BOOL
	 * Default value: FALSE
	 */
	public static final Tag<Boolean> UIAIsEnabled = from("UIAIsEnabled", Boolean.class);
	
	/**
	 * Whether this widget has keyboard focus (i.e. will receive keyboard input)
	 * From the UIA documentation:
	 *
	 * Identifies the HasKeyboardFocus property, which is a Boolean value that indicates whether the automation element has keyboard focus.
	 * Variant type: VT_BOOL
	 * Default value: FALSE
	 */
	public static final Tag<Boolean> UIAHasKeyboardFocus = from("UIAHasKeyboardFocus", Boolean.class);
	
	/**
	 * Whether this widget is currently visible on the screen. From the UIA documentation:
	 *
	 * Identifies the IsOffscreen property, which is a Boolean value that indicates whether the automation element is entirely scrolled out of view (for example, an item in a list box that is outside the viewport of the container object) or collapsed out of view (for example, an item in a tree view or menu, or in a minimized window). If the element has a clickable point that can cause it to receive the focus, the element is considered to be on-screen while a portion of the element is off-screen.
	 * The value of the property is not affected by occlusion by other windows, or by whether the element is visible on a specific monitor.
	 * If the IsOffscreen property is TRUE, the UI element is scrolled off-screen or collapsed. The element is temporarily hidden, yet it remains in the end-user's perception and continues to be included in the UI model. The object can be brought back into view by scrolling, clicking a drop-down, and so on.
	 * Objects that the end-user does not perceive at all, or that are "programmatically hidden" (for example, a dialog box that has been dismissed, but the underlying object is still cached by the application) should not be in the automation element tree in the first place (instead of setting the state of IsOffscreen to TRUE).
	 * Variant type: VT_BOOL
	 * Default value: FALSE
	 */
	public static final Tag<Boolean> UIAIsOffscreen = from("UIAIsOffscreen", Boolean.class);
	
	/**
	 * Whether this widget can be focused, such that it will receive keyboard input
	 * From the UIA documentation:
	 *
	 * Identifies the IsKeyboardFocusable property, which is a Boolean value that indicates whether the automation element can accept keyboard focus.
	 * Variant type: VT_BOOL
	 * Default value: FALSE
	 */
	public static final Tag<Boolean> UIAIsKeyboardFocusable = from("UIAIsKeyboardFocusable", Boolean.class);

	/**
	 * Accelerator key of a widget. From the UIA documentation:
	 *
	 * Identifies the AcceleratorKey property, which is a string containing the accelerator key (also called shortcut key) combinations for the automation element.
	 * Shortcut key combinations invoke an action. For example, CTRL+O is often used to invoke the Open file common dialog box. An automation element that has the AcceleratorKey property can implement the Invoke control pattern for the action that is equivalent to the shortcut command.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAAcceleratorKey = from("UIAAcceleratorKey", String.class);
	
	/**
	 * Access key of a widget. From the UIA documentation:
	 *
	 * Identifies the AccessKey property, which is a string containing the access key character for the automation element.
	 * An access key (sometimes called a mnemonic) is a character in the text of a menu, menu item, or label of a control such as a button, that activates the associated menu function. For example, to open the File menu, for which the access key is typically F, the user would press ALT+F.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAAccessKey = from("UIAAccessKey", String.class);

	/**
	 * Aria properties of a uia element. From the UIA documentation:
	 *
	 * Identifies the AriaProperties property, which is a formatted string containing the Accessible Rich Internet Application (ARIA) property information for the automation element. For more information about mapping ARIA states and properties to UI Automation properties and functions, see UI Automation for W3C Accessible Rich Internet Applications Specification.
	 * AriaProperties is a collection of Name/Value pairs with delimiters of = (equals) and ; (semicolon), for example, "checked=true;disabled=false". The \ (backslash) is used as an escape character when these delimiter characters or \ appear in the values. For security and other reasons, the provider implementation of this property can take steps to validate the original ARIA properties; however, it is not required.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAAriaProperties = from ("UIAAriaProperties", String.class);

	/**
	 * Aria role of a UIA element. From the UIA documentation:
	 *
	 * Identifies the AriaRole property, which is a string containing the Accessible Rich Internet Application (ARIA) role information for the automation element. For more information about mapping ARIA roles to UI Automation control types, see UI Automation for W3C Accessible Rich Internet Applications Specification.
	 * [!Note]
	 * As an option, the user agent can also offer a localized description of the W3C ARIA role in the LocalizedControlType property. When the localized string is not specified, the system will provide the default LocalizedControlType string for the element.
	 *
	 *
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIAAriaRole = from ("UIAAriaRole", String.class);

	/**
	 * Indicates whether input data is correct for the form rules for the UIA element/widget.
	 * From the UIA documentation:
	 *
	 * Identifies the IsDataValidForForm property, which is a Boolean value that indicates whether the entered or selected value is valid for the form rule associated with the automation element. For example, if the user entered "425-555-5555" for a zip code field that requires 5 or 9 digits, the IsDataValidForForm property can be set to FALSE to indicate that the data is not valid.
	 * Variant type: VT_BOOL
	 * Default value: FALSE
	 */
	public static final Tag<Boolean> UIAIsDataValidForForm = from("UIAIsDataValidForForm", Boolean.class);

	/**
	 * Is the UIAElement/Widget a dialog window? From theUIA documenation:
	 *
	 * Identifies the IsDialog property, which is a Boolean value that indicates whether the automation element is a dialog window. For example, assistive technology such as screen readers typically speak the title of the dialog, the focused control in the dialog, and then the content of the dialog up to the focused control ("Do you want to save your changes before closing"). For standard windows, a screen reader typically speaks the window title followed by the focused control. The IsDialog property can be set to TRUE to indicate that the client application should treat the element as a dialog window.
	 * Variant type: VT_BOOL
	 * Default value: FALSE
	 */
	public static final Tag<Boolean> UIAIsDialog = from("UIAIsDialog", Boolean.class);

	/**
	 * Does the Widget/UIAelement contain password information. From the UIA documentation:
	 *
	 * Identifies the IsPassword property, which is a Boolean value that indicates whether the automation element contains protected content or a password.
	 * When the IsPassword property is TRUE and the element has the keyboard focus, a client application should disable keyboard echoing or keyboard input feedback that may expose the user's protected information. Attempting to access the Value property of the protected element (edit control) may cause an error to occur.
	 * Variant type: VT_BOOL
	 * Default value: FALSE
	 */
	public static final Tag<Boolean> UIAIsPassword = from("UIAIsPassword", Boolean.class);

	/**
	 * Indicated whether the Widget/UIA element represents peripheral UI.
	 * From the UIA documentation:
	 *
	 * Identifies the IsPeripheral property, which is a Boolean value that indicates whether the automation element represents peripheral UI. Peripheral UI appears and supports user interaction, but does not take keyboard focus when it appears. Examples of peripheral UI includes popups, flyouts, context menus, or floating notifications. Supported starting with Windows 8.1.
	 * When the IsPeripheral property is TRUE, a client application can't assume that focus was taken by the element even if it's currently keyboard-interactive.
	 * This property is relevant for these control types:
	 * UIA_GroupControlTypeId
	 * UIA_MenuControlTypeId
	 * UIA_PaneControlTypeId
	 * UIA_ToolBarControlTypeId
	 * UIA_ToolTipControlTypeId
	 * UIA_WindowControlTypeId
	 * UIA_CustomControlTypeId
	 * Variant type: VT_BOOL
	 * Default value: FALSE
	 */
	public static final Tag<Boolean> UIAIsPeripheral = from("UIAIsPeripheral", Boolean.class);

	/**
	 * Is the widget/uiaelement required for a form? From the UIA documentation:
	 *
	 * Identifies the IsRequiredForForm property, which is a Boolean value that indicates whether the automation element is required to be filled out on a form.
	 * Variant type: VT_BOOL
	 * Default value: FALSE
	 */
	public static final Tag<Boolean> UIAIsRequiredForForm = from("UIAIsRequiredForForm", Boolean.class);

	/**
	 * The identifier of the uiaelement that labels this uiaelement. From the UIA documentation:
	 *
	 * Identifies the LabeledBy property, which is an automation element that contains the text label for this element.
	 * This property can be used to retrieve, for example, the static text label for a combo box.
	 * Variant type: VT_UNKNOWN
	 * Default value: NULL
	 */
	public static final Tag<Object> UIALabeledBy = from("UIALabeledBy", Object.class);

	/**
	 * Is the widget/uiaelement part of a landmark/group?
	 * From the UIA documentation:
	 *
	 * Identifies the LandmarkType property, which is a Landmark Type Identifier associated with an element.
	 * The LandmarkType property describes an element that represents a group of elements. For example, a search landmark could represent a set of related controls for searching.
	 * If UIA_CustomLandmarkTypeId is used then UIA_LocalizedLandmarkTypePropertyId is required to describe the custom landmark.
	 * Variant Type: VT_I4
	 * Default Value: 0
	 */
	public static final Tag<Long> UIALandmarkType = from("UIALandmarkType", Long.class);

	/**
	 * A localized string representing the type of landmark this UIA element represents.
	 * From the UIA documentation:
	 *
	 * Identifies the LocalizedLandmarkType, which is a text string describing the type of landmark that the automation element represents.
	 * This should be used in tandem with UIA_CustomLandmarkTypeId however, LocalizedLandmarkType should always take precedence over LandmarkType and be used to describe the landmark before LandmarkType.
	 * The string must match the application UI language or the operating system default UI language.
	 * Variant type: VT_BSTR
	 * Default value: empty string
	 */
	public static final Tag<String> UIALocalizedLandmarkType = from ("UIALocalizedLandmarkType", String.class);

	/**
	 * The level of the uiaelement in an hierarchical structure.
	 * From the UIA documentation:
	 *
	 * Identifies the Level property, which is a 1-based integer associated with an automation element.
	 * The Level property describes the location of an element inside a hierarchical or broken hierarchical structures. For example a bulleted/numbered list, headings, or other structured data items can have various parent/child relationships. Level describes where in the structure the item is located.
	 * It is recommended to use the CustomNavigation Control Pattern in tandem with Level.
	 * Variant type: VT_I4
	 * Default value: 0
	 */
	public static final Tag<Long> UIALevel = from("UIALevel", Long.class);

	/**
	 * From the UIA documentation:
	 *
	 * Identifies the LiveSetting property, which is supported by an automation element that represents a live region. The LiveSetting property indicates the "politeness" level that a client should use to notify the user of changes to the live region. This property can be one of the values from the LiveSetting enumeration. Supported starting with Windows 8.
	 * Variant type: VT_I4
	 * Default value: 0
	 */
	public static final Tag<Long> UIALiveSetting = from("UIALiveSetting", Long.class);

	/**
	 * The position of this UIAelement with regards to its siblings.
	 * From the UIA documentation:
	 *
	 * Identifies the PositionInSet property, which is a 1-based integer associated with an automation element. PositionInSet describes the ordinal location of the element within a set of elements which are considered to be siblings.
	 * PositionInSet works in coordination with the SizeOfSet property to describe the ordinal location in the set.
	 * Variant type: VT_I4
	 * Default value: 0
	 */
	public static final Tag<Long> UIAPositionInSet = from("UIAPositionInSet", Long.class);

	/**
	 * From the UIA documentation:
	 *
	 * Identifies the SizeOfSet property, which is a 1-based integer associated with an automation element. SizeOfSet describes the count of automation elements in a group or set that are considered to be siblings.
	 * SizeOfSet works in coordination with the PositionInSet property to describe the count of items in the set.
	 * Variant type: VT_I4
	 * Default value: 0
	 */
	public static final Tag<Long> UIASizeOfSet = from("UIASizeOfSet", Long.class);


	/**
	 * The angle of rotation of the UIA element.
	 * From the UIA documentation:
	 *
	 * Identifies the Rotation property, which specifies the angle of rotation in unspecified units.
	 * Variant type: VT_R8
	 * Default value: 0
	 */
	public static final Tag<Long> UIARotation = from("UIARotation", Long.class);

	/**
	 * From the UIA documentation:
	 *
	 * Identifies the VisualEffects property, which is a bit field that specifies effects on the automation element, such as shadow, reflection, glow, soft edges, or bevel.
	 * VisualEffects:
	 * VisualEffects_Shadow: 0x1
	 * VisualEffects_Reflection: 0x2
	 * VisualEffects_Glow: 0x4
	 * VisualEffects_SoftEdges: 0x8
	 * VisualEffects_Bevel: 0x10
	 * Variant type: VT_I4
	 * Default value: 0
	 */
	public static final Tag<Long> UIAVisualEffects = from("UIAVisualEffects", Long.class);





	/*
	Not implemented yet at this point:

	-AnnotationObjectsPropertyId
	Identifies the AnnotationObjects property, which is a list of annotation objects in a document, such as comment, header, footer, and so on.
	Variant type: VT_I4 | VT_ARRAY
	Default value: empty array

	-AnnotationTypesPropertyId
	Identifies the AnnotationTypes property, which is a list of the types of annotations in a document, such as comment, header, footer, and so on.
	Variant type: VT_I4 | VT_ARRAY
	Default value: empty array

	-CenterPointPropertyId
	Identifies the CenterPoint property, which specifies the center X and Y point coordinates of the automation element. The coordinate space is what the provider logically considers a page.
	Variant type: VT_R8 | VT_ARRAY
	Default value: VT_EMPTY

	-ClickablePointPropertyId
	Identifies the ClickablePoint property, which is a point on the automation element that can be clicked. An element cannot be clicked if it is completely or partially obscured by another window.
	Variant type: VT_R8 | VT_ARRAY
	Default value: VT_EMPTY

	-UIA_ControllerForPropertyId
	Identifies the ControllerFor property, which is an array of automation elements that are manipulated by the automation element that supports this property.
	ControllerFor is used when an automation element affects one or more segments of the application UI or the desktop; otherwise, it is hard to associate the impact of the control operation with UI elements.
	This identifier is commonly used for Auto-suggest accessibility.
	Variant type for providers: VT_UNKNOWN | VT_ARRAY
	Variant type for clients: VT_UNKNOWN (IUIAutomationElementArray )
	Default value: empty array

	-UIA_DescribedByPropertyId
	Identifies the DescribedBy property, which is an array of elements that provide more information about the automation element.
	DescribedBy is used when an automation element is explained by another segment of the application UI. For example, the property can point to a text element of "2,529 items in 85 groups, 10 items selected" from a complex custom list object. Instead of using the object model for clients to digest similar information, the DescribedBy property can offer quick access to the UI element that may already offer useful end-user information that describes the UI element.
	Variant type for providers: VT_UNKNOWN | VT_ARRAY
	Variant type for clients: VT_UNKNOWN (IUIAutomationElementArray)
	Default value: empty array

	-UIA_FillColorPropertyId
	Identifies the FillColor property, which specifies the color used to fill the automation element. This attribute is specified as a COLORREF, a 32-bit value used to specify an RGB or RGBA color.
	Variant type: VT_I4
	Default value: 0

	-UIA_FillTypePropertyId
	Identifies the FillType property, which specifies the pattern used to fill the automation element, such as none, color, gradient, picture, pattern, and so on.
	Variant type: VT_I4
	Default value: 0

	-UIA_FlowsFromPropertyId
	Identifies the FlowsFrom property, which is an array of automation elements that suggests the reading order before the current automation element. Supported starting with Windows 8.
	The FlowsFrom property specifies the reading order when automation elements are not exposed or structured in the same reading order as perceived by the user. While the FlowsFrom property can specify multiple preceding elements, it typically contains only the prior element in the reading order.
	Variant type for providers: VT_UNKNOWN | VT_ARRAY
	Variant type for clients: VT_UNKNOWN (IUIAutomationElementArray)
	Default value: empty array

	-UIA_FlowsToPropertyId
	Identifies the FlowsTo property, which is an array of automation elements that suggests the reading order after the current automation element.
	The FlowsTo property specifies the reading order when automation elements are not exposed or structured in the same reading order as perceived by the user. While the FlowsTo property can specify multiple succeeding elements, it typically contains only the next element in the reading order.
	Variant type for providers: VT_UNKNOWN | VT_ARRAY
	Variant type for clients: VT_UNKNOWN (IUIAutomationElementArray)
	Default value: empty array

	-UIA_HeadingLevelPropertyId
	Identifies the HeadingLevel property, which indicates the heading level of a UI Automation element.
	Variant type: VT_I4
	Default value: HeadingLevel_None

	-UIA_OptimizeForVisualContentPropertyId
	Identifies the OptimizeForVisualContent property, which is a Boolean value that indicates whether the provider exposes only elements that are visible. A provider can use this property to optimize performance when working with very large pieces of content. For example, as the user pages through a large piece of content, the provider can destroy content elements that are no longer visible. When a content element is destroyed, the provider should return the UIA_E_ELEMENTNOTAVAILABLE error code. Supported starting with Windows 8.
	Variant type: VT_BOOL
	Default value: FALSE

	-UIA_OutlineColorPropertyId
	Identifies the OutlineColor property, which specifies the color used for the outline of the automation element. This attribute is specified as a COLORREF, a 32-bit value used to specify an RGB or RGBA color.
	Variant type: VT_I4 | VT_ARRAY
	Default value: 0

	-UIA_OutlineThicknessPropertyId
	Identifies the OutlineThickness property, which specifies the width for the outline of the automation element.
	Variant type: VT_R8 | VT_ARRAY
	Default value: VT_EMPTY

	-UIA_SizePropertyId
	Identifies the Size property, which specifies the width and height of the automation element.
	Variant type: VT_R8 | VT_ARRAY
	Default value: VT_EMPTY

	 */
	
	////////// PATTERN AVAILABILITY PROPERTIES ////////////
	public static final Tag<Boolean> UIAIsAnnotationPatternAvailable = from("UIAIsAnnotationPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsDockPatternAvailable = from("UIAIsDockPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsDragPatternAvailable = from("UIAIsDragPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsDropTargetPatternAvailable = from("UIAIsDropTargetPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsExpandCollapsePatternAvailable = from("UIAIsExpandCollapsePatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsGridItemPatternAvailable = from("UIAIsGridItemPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsGridPatternAvailable = from("UIAIsGridPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsInvokePatternAvailable = from("UIAIsInvokePatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsItemContainerPatternAvailable = from("UIAIsItemContainerPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsLegacyIAccessiblePatternAvailable = from("UIAIsLegacyIAccessiblePatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsMultipleViewPatternAvailable = from("UIAIsMultipleViewPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsObjectModelPatternAvailable = from("UIAIsObjectModelPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsRangeValuePatternAvailable = from("UIAIsRangeValuePatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsScrollItemPatternAvailable = from("UIAIsScrollItemPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsScrollPatternAvailable = from("UIAIsScrollPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsSelectionItemPatternAvailable = from("UIAIsSelectionItemPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsSelectionPatternAvailable = from("UIAIsSelectionPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsSpreadsheetPatternAvailable = from("UIAIsSpreadsheetPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsSpreadsheetItemPatternAvailable = from("UIAIsSpreadsheetItemPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsStylesPatternAvailable = from("UIAIsStylesPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsSynchronizedInputPatternAvailable = from("UIAIsSynchronizedInputPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsTableItemPatternAvailable = from("UIAIsTableItemPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsTablePatternAvailable = from("UIAIsTablePatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsTextChildPatternAvailable = from("UIAIsTextChildPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsTextPatternAvailable = from("UIAIsTextPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsTextPattern2Available = from("UIAIsTextPattern2Available", Boolean.class);
	public static final Tag<Boolean> UIAIsTogglePatternAvailable = from("UIAIsTogglePatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsTransformPatternAvailable = from("UIAIsTransformPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsTransformPattern2Available = from("UIAIsTransformPattern2Available", Boolean.class);
	public static final Tag<Boolean> UIAIsValuePatternAvailable = from("UIAIsValuePatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsVirtualizedItemPatternAvailable = from("UIAIsVirtualizedItemPatternAvailable", Boolean.class);
	public static final Tag<Boolean> UIAIsWindowPatternAvailable = from("UIAIsWindowPatternAvailable", Boolean.class);

	////////// PATTERN PROPERTIES  ///////////

	// annotation pattern
	public static final Tag<Long> UIAAnnotationAnnotationTypeId = from("UIAAnnotationAnnotationTypeId", Long.class);
	public static final Tag<String>	UIAAnnotationAnnotationTypeName = from("UIAAnnotationAnnotationTypeName", String.class);
	public static final Tag<String>	UIAAnnotationAuthor = from("UIAAnnotationAuthor", String.class);
	public static final Tag<String> UIAAnnotationDateTime = from("UIAAnnotationDateTime", String.class);
	public static final Tag<Long> UIAAnnotationTarget = from("UIAAnnotationTarget", Long.class);

	// dock pattern
	public static final Tag<Long> UIADockDockPosition = from("UIADockDockPosition ", Long.class); // check

	// drag control pattern
	public static final Tag<String> UIADragDropEffect = from("UIADragDropEffect", String.class);
	public static final Tag<String> UIADragDropEffects = from("UIADragDropEffects", String.class); // array
	public static final Tag<Boolean> UIADragIsGrabbed = from("UIADragIsGrabbed", Boolean.class);
	public static final Tag<Object> UIADragGrabbedItems = from("UIADragGrabbedItems", Object.class); // array

	// drop target control pattern
	public static final Tag<String> UIADropTargetDropTargetEffect = from("UIADropTargetDropTargetEffect", String.class);
	public static final Tag<Long> UIADropTargetDropTargetEffects = from("UIADropTargetDropTargetEffects", Long.class); // array

	// expande/collapse pattern
	public static final Tag<Long> UIAExpandCollapseExpandCollapseState = from("UIAExpandCollapseExpandCollapseState", Long.class);

	// grid control pattern
	public static final Tag<Long> UIAGridColumnCount = from ("UIAGridColumnCount", Long.class);
	public static final Tag<Long> UIAGridRowCount = from("UIAGridRowCount", Long.class);

	// grid item control pattern
	public static final Tag<Long> UIAGridItemColumn = from("UIAGridItemColumn", Long.class);
	public static final Tag<Long> UIAGridItemColumnSpan = from("UIAGridItemColumnSpan", Long.class);
	public static final Tag<Long> UIAGridItemContainingGrid = from("UIAGridItemContainingGrid", Long.class);
	public static final Tag<Long> UIAGridItemRow = from("UIAGridItemRow", Long.class);
	public static final Tag<Long> UIAGridItemRowSpan = from("UIAGridItemRowSpan", Long.class);

	// LegacyIAccessible control pattern
	public static final Tag<Long> UIALegacyIAccessibleChildId = from ("UIALegacyIAccessibleChildId", Long.class);
	public static final Tag<String> UIALegacyIAccessibleDefaultAction = from("UIALegacyIAccessibleDefaultAction", String.class);
	public static final Tag<String> UIALegacyIAccessibleDescription = from("UIALegacyIAccessibleDescription", String.class);
	public static final Tag<String> UIALegacyIAccessibleHelp = from("UIALegacyIAccessibleHelp", String.class);
	public static final Tag<String> UIALegacyIAccessibleKeyboardShortcut = from("UIALegacyIAccessibleKeyboardShortcut", String.class);
	public static final Tag<String> UIALegacyIAccessibleName = from("UIALegacyIAccessibleName", String.class);
	public static final Tag<Long> UIALegacyIAccessibleRole = from("UIALegacyIAccessibleRole", Long.class);
	public static final Tag<Object> UIALegacyIAccessibleSelection = from("UIALegacyIAccessibleSelection", Object.class); // list/array
	public static final Tag<Long> UIALegacyIAccessibleState = from("UIALegacyIAccessibleState", Long.class);
	public static final Tag<String> UIALegacyIAccessibleValue = from("UIALegacyIAccessibleValue", String. class);

	// MultipleView control pattern
	public static final Tag<Long> UIAMultipleViewCurrentView = from("UIAMultipleViewCurrentView", Long.class);
	public static final Tag<String> UIAMultipleViewSupportedViews = from("UIAMultipleViewSupportedViews", String.class); // array

	// range value control pattern
	public static final Tag<Boolean> UIARangeValueIsReadOnly = from("UIARangeValueIsReadOnly", Boolean.class);
	public static final Tag<Double> UIARangeValueLargeChange = from("UIARangeValueLargeChange", Double.class);
	public static final Tag<Double> UIARangeValueMaximum = from("UIARangeValueMaximum", Double.class);
	public static final Tag<Double> UIARangeValueMinimum = from("UIARangeValueMinimum", Double.class);
	public static final Tag<Double> UIARangeValueSmallChange = from("UIARangeValueSmallChange", Double.class);
	public static final Tag<Double> UIARangeValueValue = from("UIARangeValueValue", Double.class);

	// selection control pattern
	public static final Tag<Boolean> UIASelectionCanSelectMultiple = from("UIASelectionCanSelectMultiple", Boolean.class);
	public static final Tag<Boolean> UIASelectionIsSelectionRequired = from("UIASelectionIsSelectionRequired", Boolean.class);
	public static final Tag<Object> UIASelectionSelection = from("UIASelectionSelection", Object.class); // array

	// selection item control pattern
	public static final Tag<Boolean> UIASelectionItemIsSelected = from("UIASelectionItemIsSelected", Boolean.class);
	public static final Tag<Long> UIASelectionItemSelectionContainer = from("UIASelectionItemSelectionContainer", Long.class);

	// spreadsheet item control panel
	public static final Tag<String> UIASpreadsheetItemFormula = from("UIASpreadsheetItemFormula", String.class);
	public static final Tag<Object> UIASpreadsheetItemAnnotationObjects = from("UIASpreadsheetItemAnnotationObjects", Object.class); //array
	public static final Tag<String> UIASpreadsheetItemAnnotationTypes = from("UIASpreadsheetItemAnnotationTypes", String.class); // array

	// scroll pattern
	public static final Tag<Boolean> UIAHorizontallyScrollable = from("UIAHorizontallyScrollable", Boolean.class);
	public static final Tag<Boolean> UIAVerticallyScrollable = from("UIAVerticallyScrollable", Boolean.class);
	public static final Tag<Double> UIAScrollHorizontalViewSize = from("UIAScrollHorizontalViewSize", Double.class);
	public static final Tag<Double> UIAScrollVerticalViewSize = from("UIAScrollVerticalViewSize", Double.class);
	public static final Tag<Double> UIAScrollHorizontalPercent = from("UIAScrollHorizontalPercent", Double.class);
	public static final Tag<Double> UIAScrollVerticalPercent = from("UIAScrollVerticalPercent", Double.class);

	// styles control pattern
	public static final Tag<String> UIAStylesExtendedProperties = from("UIAStylesExtendedProperties", String.class);
	public static final Tag<Long> UIAStylesFillColor = from("UIAStylesFillColor", Long.class);
	public static final Tag<Long> UIAStylesFillPatternColor = from("UIAStylesFillPatternColor", Long.class);
	public static final Tag<String> UIAStylesFillPatternStyle = from("UIAStylesFillPatternStyle", String.class);
	public static final Tag<String> UIAStylesShape = from("UIAStylesShape", String.class);
	public static final Tag<Long> UIAStylesStyleId = from("UIAStylesStyleId", Long.class);
	public static final Tag<String> UIAStylesStyleName = from("UIAStylesStyleName", String.class);

	// table control pattern
	public static final Tag<Long> UIATableColumnHeaders = from("UIATableColumnHeaders", Long.class); // array
	public static final Tag<Long> UIATableRowHeaders = from("UIATableRowHeaders", Long.class); // array
	public static final Tag<Long> UIATableRowOrColumnMajor = from("UIATableRowOrColumnMajor", Long.class);

	// table item control panel
	public static final Tag<Object> UIATableItemColumnHeaderItems = from("UIATableItemColumnHeaderItems", Object.class); // array
	public static final Tag<Object> UIATableItemRowHeaderItems = from("UIATableItemRowHeaderItems", Object.class); //array

	// toggle control pattern
	public static final Tag<Long> UIAToggleToggleState = from("UIAToggleToggleState", Long.class);

	// transform pattern
	public static final Tag<Boolean> UIATransformCanMove = from("UIATransformCanMove", Boolean.class);
	public static final Tag<Boolean> UIATransformCanResize = from("UIATransformCanResize", Boolean.class);
	public static final Tag<Boolean> UIATransformCanRotate = from("UIATransformCanRotate", Boolean.class);

	//transform 2 pattern
	public static final Tag<Boolean> UIATransform2CanZoom = from("UIATransform2CanZoom", Boolean.class);
	public static final Tag<Double> UIATransform2ZoomLevel = from("UIATransform2ZoomLevel", Double.class);
	public static final Tag<Double> UIATransform2ZoomMaximum = from("UIATransform2ZoomMaximum", Double.class);
	public static final Tag<Double> UIATransform2ZoomMinimum = from("UIATransform2ZoomMinimum", Double.class);

	// Value control pattern
	public static final Tag<Boolean> UIAValueIsReadOnly = from("UIAValueIsReadOnly", Boolean.class);
	public static final Tag<String> UIAValueValue = from("UIAValueValue", String.class);

	// window control pattern
	public static final Tag<Boolean> UIAWindowCanMaximize = from("UIAWindowCanMaximize", Boolean.class);
	public static final Tag<Boolean> UIAWindowCanMinimize = from("UIAWindowCanMinimize", Boolean.class);
	public static final Tag<Boolean> UIAWindowIsModal = from("UIAWindowIsModal", Boolean.class);
	public static final Tag<Boolean> UIAWindowIsTopmost = from("UIAWindowIsTopmost", Boolean.class);
	public static final Tag<Long> UIAWindowWindowInteractionState = from("UIAWindowWindowInteractionState", Long.class); // check
	public static final Tag<Long> UIAWindowWindowVisualState = from("UIAWindowWindowVisualState", Long.class); // check

	private static Set<Tag<Boolean>> patternAvailabilityTags = new HashSet<Tag<Boolean>>() {
		{
			add(UIAIsAnnotationPatternAvailable);
			add(UIAIsDockPatternAvailable);
			add(UIAIsDragPatternAvailable);
			add(UIAIsDropTargetPatternAvailable);
			add(UIAIsExpandCollapsePatternAvailable);
			add(UIAIsGridItemPatternAvailable);
			add(UIAIsGridPatternAvailable);
			add(UIAIsInvokePatternAvailable);
			add(UIAIsItemContainerPatternAvailable);
			add(UIAIsLegacyIAccessiblePatternAvailable);
			add(UIAIsMultipleViewPatternAvailable);
			add(UIAIsObjectModelPatternAvailable);
			add(UIAIsRangeValuePatternAvailable);
			add(UIAIsScrollItemPatternAvailable);
			add(UIAIsScrollPatternAvailable);
			add(UIAIsSelectionItemPatternAvailable);
			add(UIAIsSelectionPatternAvailable);
			add(UIAIsSpreadsheetPatternAvailable);
			add(UIAIsSpreadsheetItemPatternAvailable);
			add(UIAIsStylesPatternAvailable);
			add(UIAIsSynchronizedInputPatternAvailable);
			add(UIAIsTableItemPatternAvailable);
			add(UIAIsTablePatternAvailable);
			add(UIAIsTextChildPatternAvailable);
			add(UIAIsTextPatternAvailable);
			add(UIAIsTextPattern2Available);
			add(UIAIsTogglePatternAvailable);
			add(UIAIsTransformPatternAvailable);
			add(UIAIsTransformPattern2Available);
			add(UIAIsValuePatternAvailable);
			add(UIAIsVirtualizedItemPatternAvailable);
			add(UIAIsWindowPatternAvailable);
		}
	};

	static Set<Tag<Boolean>> getPatternAvailabilityTags() {
		return patternAvailabilityTags;
	}

	// we need a mapping from a control pattern availability tag to its children
	private static Map<Tag<?>, Set<Tag<?>>> controlPatternChildMapping = new HashMap<Tag<?>, Set<Tag<?>>>() {
		{
			////////// PATTERN AVAILABILITY PROPERTIES ////////////
			put(UIAIsAnnotationPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIAAnnotationAnnotationTypeId);
					add(UIAAnnotationAnnotationTypeName);
					add(UIAAnnotationAuthor);
					add(UIAAnnotationDateTime);
					add(UIAAnnotationTarget);
				}
			});
			put(UIAIsDockPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIADockDockPosition);
				}
			});
			put(UIAIsDragPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIADragDropEffect);
					add(UIADragDropEffects);
					add(UIADragIsGrabbed);
					add(UIADragGrabbedItems);
				}
			});
			put(UIAIsDropTargetPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIADropTargetDropTargetEffect);
					add(UIADropTargetDropTargetEffects);
				}
			});
			put(UIAIsExpandCollapsePatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIAExpandCollapseExpandCollapseState);
				}
			});
			put(UIAIsGridItemPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIAGridItemColumn);
					add(UIAGridItemColumnSpan);
					add(UIAGridItemContainingGrid);
					add(UIAGridItemRow);
					add(UIAGridItemRowSpan);
				}
			});
			put(UIAIsGridPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIAGridColumnCount);
					add(UIAGridRowCount);
				}
			});
			put(UIAIsInvokePatternAvailable, new HashSet<Tag<?>>() {
				{

				}
			});
			put(UIAIsItemContainerPatternAvailable, new HashSet<Tag<?>>() {
				{

				}
			});
			put(UIAIsLegacyIAccessiblePatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIALegacyIAccessibleChildId);
					add(UIALegacyIAccessibleDefaultAction);
					add(UIALegacyIAccessibleDescription);
					add(UIALegacyIAccessibleHelp);
					add(UIALegacyIAccessibleKeyboardShortcut);
					add(UIALegacyIAccessibleName);
					add(UIALegacyIAccessibleRole);
					add(UIALegacyIAccessibleSelection);
					add(UIALegacyIAccessibleState);
					add(UIALegacyIAccessibleValue);
				}
			});
			put(UIAIsMultipleViewPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIAMultipleViewCurrentView);
					add(UIAMultipleViewSupportedViews );
				}
			});
			put(UIAIsObjectModelPatternAvailable, new HashSet<Tag<?>>() {
				{

				}
			});
			put(UIAIsRangeValuePatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIARangeValueIsReadOnly);
					add(UIARangeValueLargeChange);
					add(UIARangeValueMaximum);
					add(UIARangeValueMinimum);
					add(UIARangeValueSmallChange);
					add(UIARangeValueValue);
				}
			});
			put(UIAIsScrollItemPatternAvailable, new HashSet<Tag<?>>() {
				{

				}
			});
			put(UIAIsScrollPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIAHorizontallyScrollable);
					add(UIAVerticallyScrollable);
					add(UIAScrollHorizontalViewSize);
					add(UIAScrollVerticalViewSize);
					add(UIAScrollHorizontalPercent);
					add(UIAScrollVerticalPercent);
				}
			});
			put(UIAIsSelectionItemPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIASelectionItemIsSelected);
					add(UIASelectionItemSelectionContainer);
				}
			});
			put(UIAIsSelectionPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIASelectionCanSelectMultiple);
					add(UIASelectionIsSelectionRequired);
					add(UIASelectionSelection);
				}
			});
			put(UIAIsSpreadsheetPatternAvailable, new HashSet<Tag<?>>() {
				{

				}
			});
			put(UIAIsSpreadsheetItemPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIASpreadsheetItemFormula);
					add(UIASpreadsheetItemAnnotationObjects);
					add(UIASpreadsheetItemAnnotationTypes);
				}
			});
			put(UIAIsStylesPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIAStylesExtendedProperties);
					add(UIAStylesFillColor);
					add(UIAStylesFillPatternColor);
					add(UIAStylesFillPatternStyle);
					add(UIAStylesShape);
					add(UIAStylesStyleId);
					add(UIAStylesStyleName);
				}
			});
			put(UIAIsSynchronizedInputPatternAvailable, new HashSet<Tag<?>>() {
				{

				}
			});
			put(UIAIsTableItemPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIATableItemColumnHeaderItems);
					add(UIATableItemRowHeaderItems);
				}
			});
			put(UIAIsTablePatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIATableColumnHeaders);
					add(UIATableRowHeaders);
					add(UIATableRowOrColumnMajor);
				}
			});
			put(UIAIsTextChildPatternAvailable, new HashSet<Tag<?>>() {
				{

				}
			});
			put(UIAIsTextPatternAvailable, new HashSet<Tag<?>>() {
				{

				}
			});
			put(UIAIsTextPattern2Available, new HashSet<Tag<?>>() {
				{

				}
			});
			put(UIAIsTogglePatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIAToggleToggleState);
				}
			});
			put(UIAIsTransformPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIATransformCanMove);
					add(UIATransformCanResize);
					add(UIATransformCanRotate);
				}
			});
			put(UIAIsTransformPattern2Available, new HashSet<Tag<?>>() {
				{
					add(UIATransform2CanZoom);
					add(UIATransform2ZoomLevel);
					add(UIATransform2ZoomMaximum);
					add(UIATransform2ZoomMinimum);
				}
			});
			put(UIAIsValuePatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIAValueIsReadOnly);
					add(UIAValueValue);
				}
			});
			put(UIAIsVirtualizedItemPatternAvailable, new HashSet<Tag<?>>() {
				{

				}
			});
			put(UIAIsWindowPatternAvailable, new HashSet<Tag<?>>() {
				{
					add(UIAWindowCanMaximize);
					add(UIAWindowCanMinimize);
					add(UIAWindowIsModal);
					add(UIAWindowIsTopmost);
					add(UIAWindowWindowInteractionState);
					add(UIAWindowWindowVisualState);
				}
			});
		}
	};

	public static Set<Tag<?>> getChildTags(Tag<?> patternAvailabilityTag) {
		return controlPatternChildMapping.getOrDefault(patternAvailabilityTag, null);
	}

	// a mapping from a tag to its active state
	private static Map<Tag<?>, Boolean> tagActiveMapping = new HashMap<Tag<?>, Boolean>() {
		{
			put(UIAIsAnnotationPatternAvailable, true);
			put(UIAIsDockPatternAvailable, true);
			put(UIAIsDragPatternAvailable, true);
			put(UIAIsDropTargetPatternAvailable, true);
			put(UIAIsExpandCollapsePatternAvailable, true);
			put(UIAIsGridItemPatternAvailable, true);
			put(UIAIsGridPatternAvailable, true);
			put(UIAIsInvokePatternAvailable, true);
			put(UIAIsItemContainerPatternAvailable, true);
			put(UIAIsLegacyIAccessiblePatternAvailable, true);
			put(UIAIsMultipleViewPatternAvailable, true);
			put(UIAIsObjectModelPatternAvailable, true);
			put(UIAIsRangeValuePatternAvailable, true);
			put(UIAIsScrollItemPatternAvailable, true);
			put(UIAIsScrollPatternAvailable, true);
			put(UIAIsSelectionItemPatternAvailable, true);
			put(UIAIsSelectionPatternAvailable, true);
			put(UIAIsSpreadsheetPatternAvailable, true);
			put(UIAIsSpreadsheetItemPatternAvailable, true);
			put(UIAIsStylesPatternAvailable, true);
			put(UIAIsSynchronizedInputPatternAvailable, true);
			put(UIAIsTableItemPatternAvailable, true);
			put(UIAIsTablePatternAvailable, true);
			put(UIAIsTextChildPatternAvailable, true);
			put(UIAIsTextPatternAvailable, true);
			put(UIAIsTextPattern2Available, true);
			put(UIAIsTogglePatternAvailable, true);
			put(UIAIsTransformPatternAvailable, true);
			put(UIAIsTransformPattern2Available, true);
			put(UIAIsValuePatternAvailable, true);
			put(UIAIsVirtualizedItemPatternAvailable, true);
			put(UIAIsWindowPatternAvailable, true);
			put(UIAAnnotationAnnotationTypeId, true);
			put(UIAAnnotationAnnotationTypeName, true);
			put(UIAAnnotationAuthor, true);
			put(UIAAnnotationDateTime, true);
			put(UIAAnnotationTarget, true);
			put(UIADockDockPosition, true);
			put(UIADragDropEffect, true);
			put(UIADragDropEffects, true);
			put(UIADragIsGrabbed, true);
			put(UIADragGrabbedItems, true);
			put(UIADropTargetDropTargetEffect, true);
			put(UIADropTargetDropTargetEffects, true);
			put(UIAExpandCollapseExpandCollapseState, true);
			put(UIAGridColumnCount, true);
			put(UIAGridRowCount, true);
			put(UIAGridItemColumn, true);
			put(UIAGridItemColumnSpan, true);
			put(UIAGridItemContainingGrid, true);
			put(UIAGridItemRow, true);
			put(UIAGridItemRowSpan, true);
			put(UIALegacyIAccessibleChildId, true);
			put(UIALegacyIAccessibleDefaultAction, true);
			put(UIALegacyIAccessibleDescription, true);
			put(UIALegacyIAccessibleHelp, true);
			put(UIALegacyIAccessibleKeyboardShortcut, true);
			put(UIALegacyIAccessibleName, true);
			put(UIALegacyIAccessibleRole, true);
			put(UIALegacyIAccessibleSelection, true);
			put(UIALegacyIAccessibleState, true);
			put(UIALegacyIAccessibleValue, true);
			put(UIAMultipleViewCurrentView, true);
			put(UIAMultipleViewSupportedViews, true);
			put(UIARangeValueIsReadOnly, true);
			put(UIARangeValueLargeChange, true);
			put(UIARangeValueMaximum, true);
			put(UIARangeValueMinimum, true);
			put(UIARangeValueSmallChange, true);
			put(UIARangeValueValue, true);
			put(UIASelectionCanSelectMultiple, true);
			put(UIASelectionIsSelectionRequired, true);
			put(UIASelectionSelection, true);
			put(UIASelectionItemIsSelected, true);
			put(UIASelectionItemSelectionContainer, true);
			put(UIASpreadsheetItemFormula, true);
			put(UIASpreadsheetItemAnnotationObjects, true);
			put(UIASpreadsheetItemAnnotationTypes, true);
			put(UIAHorizontallyScrollable, true);
			put(UIAVerticallyScrollable, true);
			put(UIAScrollHorizontalViewSize, true);
			put(UIAScrollVerticalViewSize, true);
			put(UIAScrollHorizontalPercent, true);
			put(UIAScrollVerticalPercent, true);
			put(UIAStylesExtendedProperties, true);
			put(UIAStylesFillColor, true);
			put(UIAStylesFillPatternColor, true);
			put(UIAStylesFillPatternStyle, true);
			put(UIAStylesShape, true);
			put(UIAStylesStyleId, true);
			put(UIAStylesStyleName, true);
			put(UIATableColumnHeaders, true);
			put(UIATableRowHeaders, true);
			put(UIATableRowOrColumnMajor, true);
			put(UIATableItemColumnHeaderItems, true);
			put(UIATableItemRowHeaderItems, true);
			put(UIAToggleToggleState, true);
			put(UIATransformCanMove, true);
			put(UIATransformCanResize, true);
			put(UIATransformCanRotate, true);
			put(UIATransform2CanZoom, true);
			put(UIATransform2ZoomLevel, true);
			put(UIATransform2ZoomMaximum, true);
			put(UIATransform2ZoomMinimum, true);
			put(UIAValueIsReadOnly, true);
			put(UIAValueValue, true);
			put(UIAWindowCanMaximize, true);
			put(UIAWindowCanMinimize, true);
			put(UIAWindowIsModal, true);
			put(UIAWindowIsTopmost, true);
			put(UIAWindowWindowInteractionState, true);
			put(UIAWindowWindowVisualState, true);
		}
	};

	public static Set<Tag<?>> getAllActiveTags() {
		return tagActiveMapping.keySet().stream().filter(tag -> tagActiveMapping.getOrDefault(tag, false)).collect(Collectors.toSet());
	}

	public static boolean tagIsActive(Tag<?> tag) {
		return tagActiveMapping.getOrDefault(tag, false);
	}

	public static Set<Tag<?>> getAllTags() {
		Set<Tag<?>> tmpList = new HashSet<>();
		tmpList.add(UIALocalizedControlType);
		tmpList.add(UIAItemType);
		tmpList.add(UIAItemStatus);
		tmpList.add(UIAProviderDescription);
		tmpList.add(UIAFullDescription);
		tmpList.add(UIAClassName);
		tmpList.add(UIAHelpText);
		tmpList.add(UIAName);
		tmpList.add(UIAControlType);
		tmpList.add(UIACulture);
		tmpList.add(UIANativeWindowHandle);
		tmpList.add(UIAOrientation);
		tmpList.add(UIAProcessId);
		tmpList.add(UIAFrameworkId);
		tmpList.add(UIAAutomationId);
		tmpList.add(UIARuntimeId);
		tmpList.add(UIAIsContentElement);
		tmpList.add(UIAIsControlElement);
		tmpList.add(UIAIsTopmostWindow);
		tmpList.add(UIAIsWindowModal);
		tmpList.add(UIAWindowInteractionState);
		tmpList.add(UIAWindowVisualState);
		tmpList.add(UIABoundingRectangle);
		tmpList.add(UIAIsEnabled);
		tmpList.add(UIAHasKeyboardFocus);
		tmpList.add(UIAIsOffscreen);
		tmpList.add(UIAIsKeyboardFocusable);
		tmpList.add(UIAAcceleratorKey);
		tmpList.add(UIAAccessKey);
		tmpList.add(UIAAriaProperties);
		tmpList.add(UIAAriaRole);
		tmpList.add(UIAIsDataValidForForm);
		tmpList.add(UIAIsDialog);
		tmpList.add(UIAIsPassword);
		tmpList.add(UIAIsPeripheral);
		tmpList.add(UIAIsRequiredForForm);
		tmpList.add(UIALabeledBy);
		tmpList.add(UIALandmarkType);
		tmpList.add(UIALocalizedLandmarkType);
		tmpList.add(UIALevel);
		tmpList.add(UIALiveSetting);
		tmpList.add(UIAPositionInSet);
		tmpList.add(UIASizeOfSet);
		tmpList.add(UIARotation);
		tmpList.add(UIAVisualEffects);
		tmpList.add(UIAIsAnnotationPatternAvailable);
		tmpList.add(UIAIsDockPatternAvailable);
		tmpList.add(UIAIsDragPatternAvailable);
		tmpList.add(UIAIsDropTargetPatternAvailable);
		tmpList.add(UIAIsExpandCollapsePatternAvailable);
		tmpList.add(UIAIsGridItemPatternAvailable);
		tmpList.add(UIAIsGridPatternAvailable);
		tmpList.add(UIAIsInvokePatternAvailable);
		tmpList.add(UIAIsItemContainerPatternAvailable);
		tmpList.add(UIAIsLegacyIAccessiblePatternAvailable);
		tmpList.add(UIAIsMultipleViewPatternAvailable);
		tmpList.add(UIAIsObjectModelPatternAvailable);
		tmpList.add(UIAIsRangeValuePatternAvailable);
		tmpList.add(UIAIsScrollItemPatternAvailable);
		tmpList.add(UIAIsScrollPatternAvailable);
		tmpList.add(UIAIsSelectionItemPatternAvailable);
		tmpList.add(UIAIsSelectionPatternAvailable);
		tmpList.add(UIAIsSpreadsheetPatternAvailable);
		tmpList.add(UIAIsSpreadsheetItemPatternAvailable);
		tmpList.add(UIAIsStylesPatternAvailable);
		tmpList.add(UIAIsSynchronizedInputPatternAvailable);
		tmpList.add(UIAIsTableItemPatternAvailable);
		tmpList.add(UIAIsTablePatternAvailable);
		tmpList.add(UIAIsTextChildPatternAvailable);
		tmpList.add(UIAIsTextPatternAvailable);
		tmpList.add(UIAIsTextPattern2Available);
		tmpList.add(UIAIsTogglePatternAvailable);
		tmpList.add(UIAIsTransformPatternAvailable);
		tmpList.add(UIAIsTransformPattern2Available);
		tmpList.add(UIAIsValuePatternAvailable);
		tmpList.add(UIAIsVirtualizedItemPatternAvailable);
		tmpList.add(UIAIsWindowPatternAvailable);
		tmpList.add(UIAAnnotationAnnotationTypeId);
		tmpList.add(UIAAnnotationAnnotationTypeName);
		tmpList.add(UIAAnnotationAuthor);
		tmpList.add(UIAAnnotationDateTime);
		tmpList.add(UIAAnnotationTarget);
		tmpList.add(UIADockDockPosition);
		tmpList.add(UIADragDropEffect);
		tmpList.add(UIADragDropEffects);
		tmpList.add(UIADragIsGrabbed);
		tmpList.add(UIADragGrabbedItems);
		tmpList.add(UIADropTargetDropTargetEffect);
		tmpList.add(UIADropTargetDropTargetEffects);
		tmpList.add(UIAExpandCollapseExpandCollapseState);
		tmpList.add(UIAGridColumnCount);
		tmpList.add(UIAGridRowCount);
		tmpList.add(UIAGridItemColumn);
		tmpList.add(UIAGridItemColumnSpan);
		tmpList.add(UIAGridItemContainingGrid);
		tmpList.add(UIAGridItemRow);
		tmpList.add(UIAGridItemRowSpan);
		tmpList.add(UIALegacyIAccessibleChildId);
		tmpList.add(UIALegacyIAccessibleDefaultAction);
		tmpList.add(UIALegacyIAccessibleDescription);
		tmpList.add(UIALegacyIAccessibleHelp);
		tmpList.add(UIALegacyIAccessibleKeyboardShortcut);
		tmpList.add(UIALegacyIAccessibleName);
		tmpList.add(UIALegacyIAccessibleRole);
		tmpList.add(UIALegacyIAccessibleSelection);
		tmpList.add(UIALegacyIAccessibleState);
		tmpList.add(UIALegacyIAccessibleValue);
		tmpList.add(UIAMultipleViewCurrentView);
		tmpList.add(UIAMultipleViewSupportedViews);
		tmpList.add(UIARangeValueIsReadOnly);
		tmpList.add(UIARangeValueLargeChange);
		tmpList.add(UIARangeValueMaximum);
		tmpList.add(UIARangeValueMinimum);
		tmpList.add(UIARangeValueSmallChange);
		tmpList.add(UIARangeValueValue);
		tmpList.add(UIASelectionCanSelectMultiple);
		tmpList.add(UIASelectionIsSelectionRequired);
		tmpList.add(UIASelectionSelection);
		tmpList.add(UIASelectionItemIsSelected);
		tmpList.add(UIASelectionItemSelectionContainer);
		tmpList.add(UIASpreadsheetItemFormula);
		tmpList.add(UIASpreadsheetItemAnnotationObjects);
		tmpList.add(UIASpreadsheetItemAnnotationTypes);
		tmpList.add(UIAHorizontallyScrollable);
		tmpList.add(UIAVerticallyScrollable);
		tmpList.add(UIAScrollHorizontalViewSize);
		tmpList.add(UIAScrollVerticalViewSize);
		tmpList.add(UIAScrollHorizontalPercent);
		tmpList.add(UIAScrollVerticalPercent);
		tmpList.add(UIAStylesExtendedProperties);
		tmpList.add(UIAStylesFillColor);
		tmpList.add(UIAStylesFillPatternColor);
		tmpList.add(UIAStylesFillPatternStyle);
		tmpList.add(UIAStylesShape);
		tmpList.add(UIAStylesStyleId);
		tmpList.add(UIAStylesStyleName);
		tmpList.add(UIATableColumnHeaders);
		tmpList.add(UIATableRowHeaders);
		tmpList.add(UIATableRowOrColumnMajor);
		tmpList.add(UIATableItemColumnHeaderItems);
		tmpList.add(UIATableItemRowHeaderItems);
		tmpList.add(UIAToggleToggleState);
		tmpList.add(UIATransformCanMove);
		tmpList.add(UIATransformCanResize);
		tmpList.add(UIATransformCanRotate);
		tmpList.add(UIATransform2CanZoom);
		tmpList.add(UIATransform2ZoomLevel);
		tmpList.add(UIATransform2ZoomMaximum);
		tmpList.add(UIATransform2ZoomMinimum);
		tmpList.add(UIAValueIsReadOnly);
		tmpList.add(UIAValueValue);
		tmpList.add(UIAWindowCanMaximize);
		tmpList.add(UIAWindowCanMinimize);
		tmpList.add(UIAWindowIsModal);
		tmpList.add(UIAWindowIsTopmost);
		tmpList.add(UIAWindowWindowInteractionState);
		tmpList.add(UIAWindowWindowVisualState);
		return tmpList;
	}

}
