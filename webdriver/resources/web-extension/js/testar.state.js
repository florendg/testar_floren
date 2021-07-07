/*
 * Keep a map with all labels client-side
 */
var labelMap;

/*
 * Get the widget tree (Chrome, Firefox) or flattened tree (Edge)
 * @param {Array} array of tags that can be skipped, like <style>, <script> etc.
 * @return {(Object | Array)}
 */
var getStateTreeTestar = function (ignoredTags) {
    var body = document.body;
    var bodyWrapped = wrapElementTestar(body, 0, 0);
    bodyWrapped['documentHasFocus'] = document.hasFocus();
    bodyWrapped['documentTitle'] = document.title;

    // Find all labels on the page
    getLabelMapTestar();

    // For Edge we return a flattened tree (as array), see comment in WdStateFetcher
    if (window.navigator.userAgent.indexOf("Edge") > -1) {
        var treeArray = [];
        traverseElementArrayTestar(treeArray, bodyWrapped, body, -1, ignoredTags);
        return treeArray;
    }
    else {
        traverseElementTestar(bodyWrapped, body, ignoredTags);
        return bodyWrapped;
    }
};

/*
 * Traverse the children from the parent element
 * @param {object} parentWrapped, the (wrapped) parent object
 * @param {node} rootElement, the body element
 * @param {object} ignoredTags, list of tags to skip, <style>, <script> etc.
 */
function traverseElementTestar(parentWrapped, rootElement, ignoredTags) {
    var childNodes = getChildNodesTestar(parentWrapped);
    for (var i = 0; i < childNodes.length; i++) {
        var childElement = childNodes[i];

        // Filter ignored tags or non-element nodes
        if (childElement.nodeType === 3) {
            parentWrapped.textContent += childElement.textContent;
            parentWrapped.textContent = parentWrapped.textContent.trim();
            continue;
        }
        if (childElement.nodeType !== 1 ||
            ignoredTags.includes(childElement.nodeName.toLowerCase())) {
            continue;
        }

        var childWrapped = wrapElementTestar(childElement, parentWrapped["xOffset"], parentWrapped["yOffset"]);
        traverseElementTestar(childWrapped, rootElement, ignoredTags);
        parentWrapped.wrappedChildren.push(childWrapped);
    }
	
	// Descend through Shadow DOM Web Elements
	if(parentWrapped.element.shadowRoot !== null){
		var shadowNodes = parentWrapped.element.shadowRoot.childNodes;
		for (var i = 0; i < shadowNodes.length; i++) {
			var childShadowElement = shadowNodes[i];
			
			// Filter ignored tags or non-element nodes
			if (childShadowElement.nodeType === 3) {
				parentWrapped.textContent += childShadowElement.textContent;
				parentWrapped.textContent = parentWrapped.textContent.trim();
				continue;
			}
			if (childShadowElement.nodeType !== 1 ||
				ignoredTags.includes(childShadowElement.nodeName.toLowerCase())) {
				continue;
			}
			
			var childShadowWrapped = wrapElementTestar(childShadowElement, parentWrapped["xOffset"], parentWrapped["yOffset"]);
			traverseElementTestar(childShadowWrapped, rootElement, ignoredTags);
			parentWrapped.wrappedChildren.push(childShadowWrapped);
		}
	}
}

/*
 * Flattened version of traverseElementTestar for Edge
 * @param {Array} treeArray, array of wrapped elements
 * @param {object} parentWrapped, the (wrapped) parent object
 * @param {node} rootElement, the body element
 * @param {integer} parentId, index of the parent in the flattened array
 * @param {object} ignoredTags, array of tags to skip, <style>, <script> etc.
 */
function traverseElementArrayTestar(treeArray, parentWrapped, rootElement, parentId, ignoredTags) {
    parentWrapped['parentId'] = parentId;
    treeArray.push(parentWrapped);

    parentId = treeArray.length - 1;

    var childNodes = getChildNodesTestar(parentWrapped);
    for (var i = 0; i < childNodes.length; i++) {
        var childElement = childNodes[i];

        // Filter ignored tags or non-element nodes
        if (childElement.nodeType === 3) {
            parentWrapped.textContent += childElement.textContent;
            parentWrapped.textContent = parentWrapped.textContent.trim();
            continue;
        }
        if (childElement.nodeType !== 1 ||
            ignoredTags.includes(childElement.nodeName.toLowerCase())) {
            continue
        }

        var childWrapped = wrapElementTestar(childElement, parentWrapped["xOffset"], parentWrapped["yOffset"]);
        traverseElementArrayTestar(treeArray, childWrapped, rootElement, parentId, ignoredTags);
    }
}

/*
 * Get all the childnodes of an (wrapped) element
 * Anticipate the use of iFrames
 * @param {object} parentWrapped, the parent element
 * @return {Array} array of the child nodes
 */
function getChildNodesTestar(parentWrapped) {
    var childNodes = parentWrapped.element.childNodes;

    // Might be contained in an iFrame
    if (childNodes.length === 0 &&
        parentWrapped.element.contentDocument !== undefined &&
        parentWrapped.element.contentDocument !== null) {
        childNodes = parentWrapped.element.contentDocument.childNodes;

        var style = getComputedStyle(parentWrapped.element);
        var left = parseInt(style.getPropertyValue('border-left-width'));
        left += parseInt(style.getPropertyValue('padding-left'));
        parentWrapped["xOffset"] = parentWrapped['rect'][0] + left;
        var top = parseInt(style.getPropertyValue('border-top-width'));
        top += parseInt(style.getPropertyValue('padding-top'));
        parentWrapped["yOffset"] = parentWrapped['rect'][1] + top;
    }

    return childNodes;
}

/*
 * Wrapping all required properties in a struct
 * This minimizes the roundtrips between Java and webdriver
 * @param {node} element, the parent HTML element
 * @param {object} xOffset, offset off the iFrame (if applicable)
 * @param {object} yOffset, offset off the iFrame (if applicable)
 * @return {object} array element and attributes
 */
function wrapElementTestar(element, xOffset, yOffset) {
    var computedStyle = getComputedStyle(element);
	
	var shadowElement = false;
	if(element.shadowRoot !== null){
		shadowElement = true;
	}

    return {
        element: element,

        attributeMap: getAttributeMapTestar(element),

        name: getNameTestar(element),
        tagName: element.tagName.toLowerCase(),
        textContent: "",
        value: element.value,
        checked: element.checked,
        selected: element.selected,
        display: computedStyle.getPropertyValue('display'),

        zIndex: getZIndexTestar(element),
        rect: getRectTestar(element, xOffset, yOffset),
        dimensions: getDimensionsTestar(element),
        isBlocked: getIsBlockedTestar(element, xOffset, yOffset),
        isClickable: isClickableTestar(element, xOffset, yOffset),
		isShadowElement: shadowElement,
        hasKeyboardFocus: document.activeElement === element,

        wrappedChildren: [],
        xOffset: xOffset,
        yOffset: yOffset
    };
}

/*
 * Determine the name for the element, optionally taken from the label
 * @param {node} the HTML element
 * @return {string} the name
 */
function getNameTestar(element) {
    var name = element.getAttribute("name");
    var id = element.getAttribute("id");

    try {
        if (id && labelMap[id]) {
            return labelMap[id];
        }
    }
    catch (err) {
    }

    if (name) {
        return name;
    }
    if (id) {
        return id;
    }
    return "";
}

/*
 * Calculate the z-index of an element
 * @param {node} the HTML element
 * @return {number} the z-index
 */
function getZIndexTestar(element) {
    if (element === document.body) {
        return 0;
    }

    if (element === null || element === undefined) {
        return 0;
    } else if (element.nodeType !== 1) {
        return getZIndexTestar(element.parentNode) + 1;
    }

    var zIndex = getComputedStyle(element).getPropertyValue('z-index');
    if (isNaN(zIndex)) {
        return getZIndexTestar(element.parentNode) + 1;
    }
    return zIndex * 1;
}

/*
 * Get the position and dimensions of the element
 * @param {node} element, the parent HTML element
 * @param {object} xOffset, offset off the iFrame (if applicable)
 * @param {object} yOffset, offset off the iFrame (if applicable)
 * @return {array} array with the position and dimensions
 */
function getRectTestar(element, xOffset, yOffset) {
    var rect = element.getBoundingClientRect();
    if (element === document.body) {
        rect = document.documentElement.getBoundingClientRect();
    }

    return [
        parseInt(rect.left) + xOffset,
        parseInt(rect.top) + yOffset,
        parseInt(element === document.body ? window.innerWidth : rect.width),
        parseInt(element === document.body ? window.innerHeight : rect.height)
    ];
}

/*
 * Get all appropriate dimensions of the element
 * Used to determine if a scrollbar is present
 * @param {node} element, the parent HTML element
 * @return {array} array with the dimensions
 */
function getDimensionsTestar(element) {
    if (element === document.body) {
        scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);
        scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
    } else {
        scrollLeft = element.scrollLeft;
        scrollTop = element.scrollTop;
    }

    var style = window.getComputedStyle(element);

    return {
        overflowX: style.getPropertyValue('overflow-x'),
        overflowY: style.getPropertyValue('overflow-y'),
        clientWidth: element.clientWidth,
        clientHeight: element.clientHeight,
        offsetWidth: element.offsetWidth || 0,
        offsetHeight: element.offsetHeight || 0,
        scrollWidth: element.scrollWidth,
        scrollHeight: element.scrollHeight,
        scrollLeft: scrollLeft,
        scrollTop: scrollTop,
        borderWidth: parseInt(style.borderLeftWidth, 10) + parseInt(style.borderRightWidth, 10),
        borderHeight: parseInt(style.borderTopWidth, 10) + parseInt(style.borderBottomWidth, 10)
    };
}

/*
 * Determine if the document body exceeds the height of the browser window
 * Used to determine if the browser contains a vertical scrollbar
 * @return {bool} true if the document body is higher
 */
function isPageVerticalScrollable(){
	return (document.body.clientHeight > window.innerHeight);
}

/*
 * Determine if the document body exceeds the width of the browser window
 * Used to determine if the browser contains a horizontal scrollbar
 * @return {bool} true if the document body is wider
 */
function isPageHorizontalScrollable(){
	return (document.body.clientWidth > window.innerWidth);
}

/*
 * Determin if an element is blocked by another element,
 * but does not contain said element (e.g. <img> inside <a>)
 * @param {node} element, the parent HTML element
 * @param {object} xOffset, offset off the iFrame (if applicable)
 * @param {object} yOffset, offset off the iFrame (if applicable)
 * @return {bool} true if the element is blocked by another element
 */
function getIsBlockedTestar(element, xOffset, yOffset) {
    // get element at element's (click) position
    var rect = element.getBoundingClientRect();
    var x = rect.left + rect.width / 2 + xOffset;
    var y = rect.top + rect.height / 2 + yOffset;
    var elem = document.elementFromPoint(x, y);

    // element is inside iframe(s)
    var outer = undefined;
    while (elem instanceof HTMLIFrameElement ||
           elem instanceof HTMLFrameElement ||
           (outer !== undefined && outer.contentWindow !== undefined &&
            elem instanceof outer.contentWindow.HTMLIFrameElement)) {
        outer = elem;

        // Adjust according to position of the iframe
        var tmp = elem.getBoundingClientRect();
        x -= tmp.x;
        y -= tmp.y;

        // Elements from a cross-origin frame are not reachable
        try {
            elem = elem.contentWindow.document.elementFromPoint(x, y);
        }
        catch(exception) {
            return true
        }
    }

    // elem can not be found, asssume the originating element is not blocked
    if (elem === null) {
        return false;
    }

    // Ignore encapsulated childs of <a>
    if (element.tagName === "A" && element.contains(elem)) {
        return false;
    }

    // Ignore "label for"
    if (elem.tagName === "LABEL") {
        return false;
    }

    // return whether obscured element has same parent node
    // (will also return false if element === elem)
    return elem.parentNode !== element.parentNode;
}

/*
 * Determine if an element is clickable
 * @param {node} element, the parent HTML element
 * @return {bool} true if the element is clickable
 */
function isClickableTestar(element) {
    // onClick defined as tag attribute
    if (element.getAttribute("onclick") !== null) {
        return true;
    }
    // No prototype patching within dynamically loaded iFrames
    if (element.getEventListeners === undefined) {
        return false;
    }
    // onClick added via JS
    var arr = element.getEventListeners('click');
    return arr !== undefined && arr.length > 0;
}

/*
 * Fill the labelMap for all HTML elements on the page that have labels
 */
function getLabelMapTestar() {
    labelMap = {};
    var labelList = document.getElementsByTagName("LABEL");
    for (var i = 0; i < labelList.length; i++) {
        var item = labelList[i];
        if (item.getAttribute("for")) {
            labelMap[item.getAttribute("for")] = item.textContent;
        }
    }
}

/*
 * Get all the attributes for an HTML element
 * @param {node} element, the parent HTML element
 * @return {array} array of name, value pairs
 */
function getAttributeMapTestar(element) {
    return Array.from(element.attributes).reduce(function (map, att) {
        map[att.name] = att.nodeValue;
        return map;
    }, {});
}
