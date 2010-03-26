var Tutorial = function() {
}

Tutorial.prototype = {
  index: function() {
    return P('Use `tutorial &lt;chapter&gt; &lt;section&gt;`') + 
           P('Available sections are:') +
           DIV(LIST(['1. What is Gremlin?', 
                     '2. What can I do with Gremlin?' +
                        LIST([
                          'a. Defining variables.',
                          'b. Using ”$_” and ”$_g” and ”.” special variables.',
                          'c. Using Gremlin build-in functions and data structures (maps, lists).',
                          'd. Gremlin Loops (foreach, while).',
                          'e. Defining custom functions/paths.',
                          'f. Basic graph traversals (including load graph data from files).',
                          'g. Using different backends (Neo4j, TinkerGraph, Sail).',
                          'h. Spreading Activation algorithm.'
                        ]), 
                     '3. Acknowledgements'
                    ]));
  },
  
  _p1: function() {
    return P("Chapter 1");
  },
  
  _p2: function(sectionId) {
    
    this._annotation = function() {
      return P("Chapter 2 - What can I do with Gremlin?");
    }

    this._a = function() {
      return P("Chapter 2a."); 
    }
    
    this._b = function() {
    }

    this._c = function() {
    }

    this._d = function() {
    }

    var section = this['_' + sectionId];
    return ((section) ? section() : this._annotation());
  },

  _p3: function() {
    return P("Chapter 3");
  },

  handle: function(req) {
    // format is help <paragraph> <section>
    // example - help 2 a
    var currentReqParts = req.split(" ");
    var chapter = this['_p' + currentReqParts[1]];
    return ((chapter) ? chapter(currentReqParts[2]) : this.index());
  },
};

