// assume underscore is loaded

function makeTemplate(structure) {
  var t = _.template(structure);
  // content in string form, will be parsed to JSON
  function tmpl(content) {
    var data = JSON.parse(content);
    return t(data);
  }
  return tmpl;
}
