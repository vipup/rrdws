OpenShakespeare = ("OpenShakespeare" in window) ? OpenShakespeare : {}

OpenShakespeare.Annotator = function (element) {
  var $ = jQuery, self = this

  this.annotator = $(element).annotator().data('annotator')
  this.currentUser = null

  this.options = {
    user: { },

    store: {
      prefix: 'http://c0824ce0:8080/examples/api.jsp?we=qwe&'
    }
  }

  // Init
  ;(function () {
     self.annotator.addPlugin("Permissions", self.options.user)
     self.annotator.addPlugin("Store", self.options.store)
  })()

  this.setCurrentUser = function (user) {
    self.annotator.plugins["Permissions"].setUser(user)
  }

  return this
}
