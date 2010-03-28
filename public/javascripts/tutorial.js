var Tutorial = function() {
    this.prev = this.index;
    this.next = this._p1;
}

Tutorial.prototype = {
  index: function() {
      this.next = this._p1;
      return P('Use `tutorial &lt;chapter&gt; &lt;section&gt;` or `next` `prev` commands to navigate.') + BR() +
          P('Available sections are:') +
          DIV(LIST([
                     '1. What is Gremlin?', 
                     '2. What can I do with Gremlin?' +
                        LIST([
                          'a. Defining variables.',
                          'b. Using ”$_” and ”$_g” and ”.” special variables.',
                          'c. Using Gremlin build-in functions and data structures (maps, lists).',
                          'd. Gremlin Loops (foreach, while).',
                          'e. Defining custom functions/paths.',
                          'f. Basic graph traversals.'
                        ]), 
                     '3. Acknowledgements'
                    ]));
  },
  
  _p1: function() {
    this.prev = this.index;
    this.next = this._p2;
    return P("Chapter 1 - What is Gremlin?") + BR() +  
           P('&nbsp;&nbsp;Gremlin is a domain specific programming language for ' + LINK("http://en.wikipedia.org/wiki/Graph_%28mathematics%29", "graphs") + '. Graphs are data structures where there exists vertices (i.e. dots, nodes) and edges (i.e. lines, arcs). Gremlin was designed to work with a type of graph called a property graph. Property graphs are defined, in detail, in the ' + LINK("http://wiki.github.com/tinkerpop/gremlin/defining-a-property-graph", "Defining a Property Graph") + ' section of ' + LINK("http://wiki.github.com/tinkerpop/gremlin/", "complete documentation") + '. Gremlin makes extensive use of ' + LINK("http://www.w3.org/TR/xpath", "XPath 1.0") + ' to define abstract path descriptions (path expressions) through a graph. It is important to learn and understand XPath as this will make it easier to understand Gremlin.'); 
  },
  
  _p2: function(sectionId) {
    
    this._annotation = function() {
      this.prev = this._p1;
      this.next = this._a;
      return P("Chapter 2 - What can I do with Gremlin?") + BR() +
             P("&nbsp;&nbsp;Before diving into the specifics of Gremlin, its good to know what you are getting yourself into. Moreover, its important to know if Gremlin can be of use to you. Below is a list of a few key benefits of Gremlin:") + BR() +
             DIV(LIST(
                 [
                     '1. Gremlin is useful for manually working with your graph;',
                     '2. Gremlin allows you to query a graph;',
                     '3. Gremlin can express complex graph traversals succinctly;',
                     '4. Gremlin is useful for exploring and learning about graphs;',
                     '5. Gremlin allows you to explore the Semantic Web/Web of Data;',
                     '6. Gremlin allows for universal path-based computations.'
                 ]));
    }

    this._a = function() {
      this.prev = this._annotation;
      this.next = this._b;
 
      return P("Chapter 2a - Defining variables.") + BR() +
             P("&nbsp;&nbsp;Gremlin gives you possibility to work with variables.") + BR() +
             P("&nbsp;&nbsp;Variables in Gremlin must be proceeded by a $ character.") + 
             P("&nbsp;&nbsp;The assignment operator is ':=' and it is used to assign a value to a variable or an element to a list or map:") + BR() + 
                PLIST("$foo := 'bar'") + PLIST("$i := 1 + 5"); 
    }
    
    this._b = function() {
      this.prev = this._a;
      this.next = this._c;

      return P("Chapter 2b - Using ”$_” and ”$_g” and ”.” special variables.") + BR() +
             P("&nbsp;There are three special variables in Gremlin ”$_” and ”$_g” and ”.”:") + BR() +
             PLIST("• ”$_”  is a reserved variable that denotes the root list. In this way, the root list can be redefined.") +
             PLIST("• ”$_g” denotes the graph object. It allows the user to assign a working graph that will be referenced by graph functions when no graph argument is provided.") +
             PLIST("• ”.” denotes reference to the root list.");
    }

    this._c = function() {
      this.prev = this._b;
      this.next = this._d;

      return P("Chapter 2c - Using Gremlin build-in functions and data structures (maps, lists).") + BR() +
             P("&nbsp;Gremlin provides build-in functions and data structures which will be very useful while working with graphs.") + BR() +
             P("To execute a function you should call it using special format - ”&lt;prefix&gt;:&lt;function_name&gt;(&lt;arg&gt;, ...)”:") + BR() +
             PLIST("g:print('hello world!') - will execute build-in print function.") + BR() +
             P("or without arguments:") + BR() +
             PLIST("g:print() - will print empty string.") + BR() +
             P("There are functions which could be referenced without &lt;prefix&gt; - global functions - like: null(), false(), true()") + BR() +
             PLIST("$foo := false() - value returned by false() will be assigned to $foo variable.") + BR() +
             P("&nbsp;Gremlin has own implementation of Map and List data structures (will be familiar to Java developers):") + BR() +
             PLIST("g:map(&lt;key&gt;, &lt;value&gt;, ...) - function used to construct map objects:") +
             PLIST("g:map('foo', 'bar') - will return {'foo'='bar'} map.") + BR() +
             P("the same goes for List:") + BR() + 
             PLIST("g:list(&lt;value&gt;,...) - function used to construct list objecs:") +
             PLIST("g:list(1,2,3,4) - will return [1.0, 2.0, 3.0, 4.0].") + BR() +
             P("result of map or list function could be assigned to a variable:") + BR() +
             PLIST("$foo := g:map('foo', 'bar')") + 
             PLIST("$foo := g:list('foo', 'bar')") + BR() + 
             P("to get value from map use g:get(element, string) function:") + BR() +
             PLIST("g:get(g:map('foo', 'bar'), 'foo') - returns 'bar'") + BR() + 
             P("g:get(list, number) function used to get values from list:") + BR() +
             PLIST("g:get(g:list(3, 4), 1) - returns '3.0'") + BR() +
             P("to assign new elements to map use g:assign(map,object,object) function:") + BR() +
             PLIST("$foo := g:map('foo', 'bar')") +
             PLIST("g:assign($foo, 'foo2', 'bar2') - returns 'bar2'") +
             PLIST("g:print($foo) - returns {foo2=bar2, foo=bar}") + BR() +
             P("Gremlin Function Library Reference could be found " + LINK("http://wiki.github.com/tinkerpop/gremlin/gremlin-function-library", "here"));
    }

    this._d = function() {
      this.prev = this._c;
      this.next = this._e;

      return P("Chapter 2d - Gremlin Loops (foreach, while).") + BR() +
             P("&nbsp;Gremlin also has build-in loop support - foreach and while:") + BR() +
             P("1. Foreach") + BR() + 
             P("&nbsp;The foreach statement will loop over its body the number of times as there are values in the provided loop list. Each item in the list is assigned to a variable and that variable can be referenced in the loop body. The generic structure and example of foreach is provided below.") + BR() +
             PLIST("foreach variable in xpath_list<br/>&nbsp;&nbsp;statement*<br />end") + BR() +
             P("Here is a little example how to use it:") + BR() + 
             PLIST("$i := 0") +
             PLIST("foreach $j in 1 | 2 | 3") +
             PLIST("&nbsp;&nbsp;$i := $i + $j") +
             PLIST("end") + BR() +
             P("this will return - 6.0.") + BR() + 
             P("2. While") + BR() + 
             P("&nbsp;The while statement will loop over its body until the provided condition is met. The generic structure and example of while is provided below.") + BR() +
             PLIST("while xpath_boolean<br/>&nbsp;&nbsp;statement*<br/>end") + BR() +
             P("Here is a little example how to use it:") + BR() + 
             PLIST("$i := 'g'") + 
             PLIST("while not(matches($i,'ggg'))") +
             PLIST("&nbsp;&nbsp;$i := concat($i,'g')") + 
             PLIST("end") + BR() +
             P("this will return - 'ggg'");
    }

    this._e = function() {
      this.prev = this._d;
      this.next = this._f;

      return P("Chapter 2e - Defining custom functions/paths.") + BR() +
             P("&nbsp;Gremlin gives you possibility to define custom functions and paths.") + BR() + 
             P("Function can be defined using following syntax:") + BR() +
             PLIST("func &lt;prefix&gt;:&lt;functiona-name&gt;($var, ...)<br/>&nbsp;&nbsp;statement*<br/>end") + 
             P("Example") + BR() +
             PLIST("func u:hello-name($name)<br/>&nbsp;&nbsp;g:print(concat('hello ', $name))<br/>end") + BR() +
             P("and if you then run `u:hello-name('pavel')` result will be 'hello pavel'.") + 
             P("Please note - there are no return statement - function returns value of its last statement!") + BR() +
             P("Path can be defined using following syntax:") + BR() + 
             PLIST("path string<br/>&nbsp;&nbsp;statement*<br/>end") + BR() +
             P("Example") + BR() +
             PLIST("path co-developer<br/>&nbsp;&nbsp;./outE[@label='created']/inV/inE[@label='created']/outV[g:except($_)]<br/>end");
    }

    this._f = function() {
      this.prev = this._e;
      this.next = this._p3;

      return P("Chapter 2f - Basic graph traversals.") + BR() + 
             P("&nbsp;First of all we should learn how to open graph and how to load graph data from file.") +
             P("As Gremlin has alot of backends graph could be opened using different functions, most common if then are:") + BR() +
             PLIST("1. tg:open() - used to open TinkerGraph;") + 
             PLIST("2. neo4j:open(&lt;database-name-as-string&gt;) - used to open Neo4j database connection;") + 
             PLIST("3. sail:open() - used to open SAIL memorystore.") + BR() +
             P("Each of those functions return unified graph object which could be assigned to a variable. Gremlin has special variable for this aim - '$_g'. When we have your graph opened next thing to do is to set root list to '$_' variable (if there are any existing vertices in the graph):") + BR() +
             PLIST("$_ := g:id('1') - function g:id(graph?, id) used to get vertex from graph object by its id. If you have $_g variable assigned then you don't have to provide graph? argument to the g:id function.") + 
             ("After $_ is set you can access your root list by using '.' statement") + BR() +
             P("Here is the list of most used functions to work with graphs:") + BR() +
             PLIST("1. vertex g:id(graph?, id) - get vertex by id.") +
             PLIST("2. vertex g:add-v(graph?, object?, object?) - add new vertex with given attributes, e.g. g:add-v($g, g:map('name', 'pavel')).") +
             PLIST("3. edge g:add-e(graph?, object?, vertex, string, vertex) - add new edge connecting to vertexes, e.g. g:add-e($g, $v1, 'knows', $v2).") +
             PLIST("4. boolean g:save(string) - save graph data into XML file with given name, e.g. g:save('my-graph').") + 
             PLIST("5. boolean g:load(graph?, string) - load graph data from XML file with given name to graph object variable, e.g. g:load($g, 'my-graph').") +
             PLIST("6. boolean g:clear(graph?) - clear contents of the graph, e.g. g:clear($g).") + 
             PLIST("7. boolean g:close(graph?) - close given graph.") + BR() +
             P("Lets now make a simple graph traversal:") + BR() + 
             PLIST("# open a graph object") + 
             PLIST("$_g := tg:open()") + BR() +
             PLIST("# assign root list to a new vertex") +
             PLIST("$_ := g:add-v()") + BR() +
             PLIST("# assign @name attribute to the root vertex") + 
             PLIST("./@name := 'pavel'") + BR() +
             PLIST("# create new vertex and assign it to $marko variable") + 
             PLIST("$marko := g:add-v()") + BR() + 
             PLIST("# add @name attribute as we did before") +
             PLIST("$marko/@name := 'marko'") + BR() +
             PLIST("# add edge to connect pavel and marko") + 
             PLIST("g:add-e($_, 'knows', $marko)") + BR() +
             PLIST("# lets traverse our graph from root vertex to find out outgoing edges") + 
             PLIST("./outE") + BR() +
             PLIST("# lets get ingoing vertexs to your outgoing edge") +
             PLIST("./outE/inV") + BR() + 
             PLIST("# let get a @name attribute of found vertex") +
             PLIST("./outE/inV/@name") + BR() +
             PLIST("# lets add few more vertices and search by criteria") + 
             PLIST("$max := g:add-v()") +
             PLIST("$max/@name := 'max'") + 
             PLIST("$max/@age := 23") +
             PLIST("$liza := g:add-v()") +
             PLIST("$liza/@name := 'liza'") +
             PLIST("$liza/@age := 15") +
             PLIST("g:add-e($_, 'knows', $max'") +
             PLIST("g:add-e($_, 'has syster', $liza)") +
             PLIST("# filter by 'knows' edges") + 
             PLIST("./outE[@label = 'knows']/inV/@name") +
             PLIST("# all outgoing edges with label 'knows' and age more then 20 years") + 
             PLIST("./outE[@label = 'knows']/inV[@age > 20]/@name") + BR() +
             PLIST("# lets save and close the graph and then load data from file") +
             PLIST("g:save('my-graph')") +
             PLIST("g:close()") +
             PLIST("g:load('my-graph')") + BR() +
             P("For bigger example please visit " + LINK("http://wiki.github.com/tinkerpop/gremlin/basic-graph-traversals", "wiki")); 
    }

    var section = this['_' + sectionId];
    return ((section) ? section() : this._annotation());
  },

  _p3: function() {
    this.prev = this._p2;
    this.next = this._p1;
    return P("Chapter 3 - Acknowledgements.") + BR() +
           P("Special thanks to:") + BR() +
           PLIST(LINK("http://www.linkedin.com/in/neubauer", "Peter Neubauer")) + 
           PLIST(LINK("http://markorodriguez.com/", "Marko A. Rodriguez"));
  },

  handle: function(req) {
    if($.trim(req) == 'prev') return this.prev();
    if($.trim(req) == 'next') return this.next();

    // format is help <paragraph> <section>
    var currentReqParts = req.split(" ");
    var chapter = this['_p' + currentReqParts[1]];
    return ((chapter) ? chapter(currentReqParts[2]) : this.index());
  }
};

