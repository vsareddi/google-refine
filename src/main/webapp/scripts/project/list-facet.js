function ListFacet(div, config, options) {
    this._div = div;
    this._config = config;
    this._selection = [];
    this._data = null;
    
    this.render();
}

ListFacet.prototype.getJSON = function() {
    var o = cloneDeep(this._config);
    o.type = "list";
    o.selection = [];
    for (var i = 0; i < this._selection.length; i++) {
        var choice = cloneDeep(this._selection[i]);
        choice.s = true;
        o.selection.push(choice);
    }
    return o;
};

ListFacet.prototype.updateState = function(data) {
    this._data = data;
    
    var selection = [];
    var choices = data.choices;
    for (var i = 0; i < choices.length; i++) {
        var choice = choices[i];
        if (choice.s) {
            selection.push(choice);
        }
    }
    this._selection = selection;
    
    this.render();
};

ListFacet.prototype.render = function() {
    var self = this;
    
    var scrollTop = 0;
    try {
        scrollTop = this._div[0].childNodes[1].scrollTop;
    } catch (e) {
    }
    var container = this._div.empty();
    
    var headerDiv = $('<div></div>').addClass("facet-title").appendTo(container);
    $('<span></span>').text(this._config.name).appendTo(headerDiv);
    
    var bodyDiv = $('<div></div>').addClass("facet-body").appendTo(container);
    if (this._data == null) {
        bodyDiv.html("Loading...");
    } else {
        var selectionCount = this._selection.length;
        if (selectionCount > 0) {
            var reset = function() {
                self._reset();
            };
            $('<a href="javascript:{}"></a>').addClass("facet-choice-link").text("reset").click(reset).prependTo(headerDiv);
        }
        
        var renderChoice = function(choice) {
            var label = choice.v.l;
            var count = choice.c;
            
            var choiceDiv = $('<div></div>').addClass("facet-choice").appendTo(bodyDiv);
            if (choice.s) {
                choiceDiv.addClass("facet-choice-selected");
            }
            
            var a = $('<a href="javascript:{}"></a>').addClass("facet-choice-label").text(label).appendTo(choiceDiv);
            $('<span></span>').addClass("facet-choice-count").text(count).appendTo(choiceDiv);
            
            var select = function() {
                self._select(choice, false);
            };
            var selectOnly = function() {
                self._select(choice, true);
            };
            var deselect = function() {
                self._deselect(choice);
            };
            
            if (choice.s) { // selected
                if (selectionCount > 1) {
                    // select only
                    a.click(selectOnly);
                } else {
                    // deselect
                    a.click(deselect);
                }
                
                // remove link
                $('<a href="javascript:{}"></a>').addClass("facet-choice-link").text("remove").click(deselect).prependTo(choiceDiv);
            } else if (selectionCount > 0) {
                a.click(selectOnly);
                
                // include link
                $('<a href="javascript:{}"></a>').addClass("facet-choice-link").text("include").click(select).appendTo(choiceDiv);
            } else {
                a.click(select);
            }
            
        };
        
        var choices = this._data.choices;
        for (var i = 0; i < choices.length; i++) {
            renderChoice(choices[i]);
        }
        
        bodyDiv[0].scrollTop = scrollTop;
    }
};

ListFacet.prototype._select = function(choice, only) {
    if (only) {
        this._selection = [];
    }
    this._selection.push(choice);
    this._updateRest();
};

ListFacet.prototype._deselect = function(choice) {
    for (var i = this._selection.length - 1; i >= 0; i--) {
        if (this._selection[i] == choice) {
            this._selection.splice(i, 1);
            break;
        }
    }
    this._updateRest();
};

ListFacet.prototype._reset = function() {
    this._selection = [];
    this._updateRest();
};

ListFacet.prototype._updateRest = function() {
    ui.browsingEngine.update();
    ui.dataTableView.update(true);
};