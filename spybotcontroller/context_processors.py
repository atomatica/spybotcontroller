from django.conf import settings

def site_settings(request):
    return {
        'SPYBOT_URL' : settings.SPYBOT_URL,
        'SITE_URL' : settings.SITE_URL,
        'STATIC_URL' : settings.STATIC_URL,
    }
