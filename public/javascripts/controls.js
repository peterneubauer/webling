/**
 * Shortcut control panel for Webling.
 * @author Jacob Hansson <jacob@voltvoodoo.com>
 */

// Ensure Webling namespace is defined
var Webling = Webling || {};

/**
 * This defines the buttons available in the Webling controls menu.
 * The shortcuts variable is an array of buttons and headlines.
 * 
 * Buttons are objects with two required attributes, "label" and "commands". The label will be displayed on the button,
 * the commands attribute is an array of commands to execute in the terminal. The commands can either be strings or functions.
 * In the latter case the function will be executed and the result will be executed in the terminal.
 * 
 * Headlines are objects with one required attribute, "headline", and one optional, "children". The headline attribute is the
 * headline text. The children attribute is an array that follows the same rules as the shortcuts array itself.
 */
Webling.shortcuts = [
   { headline: 'Load datasets', children : [
      { label : 'Simple Graph', commands : 
         ['$_g := tg:open()',
          'g:load("http://github.com/tinkerpop/gremlin/raw/master/data/graph-example-1.xml")',
          '$_ := g:id("1")',
          './outE']},
      { label : 'Custom GraphML', commands : 
         ['$_g := tg:open()',
          function() {
             var url = prompt("Enter a url (including http://) for a graphml dataset to load:");
             return 'g:load("' + url + '")';
          },
          '$_ := g:id("1")',
          './outE']}
   ]},
   { headline: 'Tutorial', children : [
      { label : 'What is Gremlin?', commands : ['tutorial 1']},
      { label : 'What can I do with Gremlin?', commands : ['tutorial 2']},
      { label : '<span class="submenu"> - Variables</span>', commands : ['tutorial 2 a']},
      { label : '<span class="submenu"> - Special variables</span>', commands : ['tutorial 2 b']},
      { label : '<span class="submenu"> - Built in data structures</span>', commands : ['tutorial 2 c']},
      { label : '<span class="submenu"> - Loops</span>', commands : ['tutorial 2 d']},
      { label : '<span class="submenu"> - Functions and Paths</span>', commands : ['tutorial 2 e']},
      { label : '<span class="submenu"> - Basic graph traversals</span>', commands : ['tutorial 2 f']},
   ]}
];

/**
 * Webling.Controls
 * 
 * Handles a shortcut-menu for the Webling terminal. This class also provides public
 * methods for creating callbacks for buttons meant to manipulate the terminal, see Webling.Controls.createCallback().
 * 
 * @param jQuery is a jQuery instance
 * @param container is a jQuery element to inject the menu into, needs to be a UL
 * @param terminal is the Webling terminal instance to manipulate
 * @param shortcuts is a shortcut menu descriptor array, see the beginning of this file.
 * @return the Controls singleton instance
 * 
 */
Webling.Controls = function(jquery, container, terminal, shortcuts) {

   var self = this;
   
   // 
   // PRIVATE
   //

   self.container = container;
   self.$         = jquery;
   self.terminal  = terminal;

   /**
    * Add an array of shortcuts to the menu container. See the beginning of controls.js for
    * a definition of how the shortcutList array should be formatted.
    * @param shortcutList is an array that defines shortcut buttons and headlines
    * @param container (optional) is the container element for the list
    */
   self.addShortcuts = function(shortcutList, container) {
      container = container || self.container;

      var subContainer;
      for(var i = 0; i < shortcutList.length; i++) {
         if(shortcutList[i].headline) {
            subContainer = self.addHeadline(shortcutList[i].headline, container);

            if(shortcutList[i].children) {
               self.addShortcuts( shortcutList[i].children, subContainer );
            }

         } else {
            self.addShortcut(shortcutList[i], container);
         }
      }
   }

   /**
    * Add a single shortcut button to a given UL element.
    * @param shortcut is a shortcut object, as defined in the beginning of the controls.js file.
    * @param container (optional) is a UL element to append the button to. Default is the container specified for this Controls 
    * instance.
    * @return the button element
    */
   self.addShortcut = function(shortcut, container) {
      var button = self.$("<li><a href='#'>" + shortcut.label + "</a></li>");

      // Add command-processing callback to button
      button.click( self.createCallback( shortcut.commands) );

      container.append(button);
      return button;
   }

   /**
    * Add a headline element to a given UL element.
    * @param label is the string to use as headline label
    * @param container (optional) is a UL element to append the headline to. Default is the container specified for this Controls 
    * instance.
    * @return the ul element created around the headline
    */
   self.addHeadline = function(label, container) {
      var ul = self.$("<ul><li class='control-headline'>" + label + "</li></ul>");
      var li = self.$("<li></li>");
      li.append(ul);
      container.append(li);
      return ul;
   }

   /**
    * Create a jQuery event callback, connected to the terminal defined for this Controls instance, that when executed runs a series
    * of commands on the terminal. Note that this callback per default will prevent the default action of whatever event you 
    * bind it to.
    * @param command is an array of strings to execute or methods that return strings to execute, or a mix thereof
    * @param preventDefault (optional) boolean, set to false to not prevent the default action of the event this
    * callback is connected to.
    * 
    * @return the callback
    */
   self.createCallback = function(commandList, preventDefault) {
      var preventDefault = preventDefault || true;
      return function(ev) {
      
         // Stop default action of button
         if(preventDefault) {
            ev.preventDefault();
         }

         // Create a shallow copy of the commands to be executed
         var commands = self.quickClone(commandList);

         // This is triggered once for each command, shifting off one command for each call
         function processCommand() {
            if ( commands.length > 0 ) {
               var nextCommand = commands.shift();

               // If the specified command is a function, execute it and use the returned string
               if ( typeof(nextCommand) === 'function') {
                  nextCommand = nextCommand();
               }
 
               self.terminal.activeLine[0].value = nextCommand;
               self.terminal.processInput(self.terminal.activeLine[0].value, processCommand);
            }
         }

         // Start processing the first command
         processCommand();
   
      }
   }

   /** 
    * Internal, quick-and-dirty shallow copy implementation.
    */
   self.quickClone = function(original) {
      var clone = [];      
      for(var i = 0; i < original.length; i++) {
         clone[i] = original[i];
      }
      return clone;
   }
   
   // Initiate menu
   self.addShortcuts(shortcuts);

   //
   // PUBLIC INTERFACE
   //

   return {

      addShortcuts   : self.addShortcuts,

      addShortcut    : self.addShorcut,

      addHeadline    : self.addHeadline,

      createCallback : self.createCallback

   };

};
