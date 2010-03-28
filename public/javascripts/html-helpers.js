var P = function(content) {
  return "<p>" + content + "</p>";
}

var PLIST = function(content) {
  return '<p style="padding-left: 32px;padding-top: 5px;">' + content + '</p>';
}

var DIV = function(content) {
  return "<div style='color: #45FF17;'>" + content + "</div>";
}

var LIST = function(sections) {
  var sectionsInLi = '';

  for(i = 0; i < sections.length; i++) {
    sectionsInLi += '<li>' + sections[i] + '</li>';
  }

  return '<ol style="padding: 12px;">' + sectionsInLi + '</ol>';
}

var LINK = function(url, label) {
  return '<a href="' + url + '" target="_blank">' + label + '</a>';
}

var BR = function() {
  return "<br />";
}

