from django.conf.urls.defaults import patterns, include, url
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    url(r'^spybot$', 'spybotcontroller.ui.views.home', name='home'),
    url(r'^spybot/cmd/(?P<cmd>\w+)/$', 'spybotcontroller.ui.views.cmd'),
    url(r'^spybot/cmd/(?P<cmd>\w+)/(?P<value>\d+)/$', 'spybotcontroller.ui.views.cmd'),
    url(r'^spybot/admin/', include(admin.site.urls)),
)
