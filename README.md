LibLoader
====================

This is a tiny helper class for loading versioned native libraries in Android.

<p>Android can't properly handle versioned libraries like libfoo.so.1.2. That's not a big deal for a stand alone lib:
You can just strip the version from the lib name, i.e. rename it to libfoo.so and use it as if it was unversioned.</p>

<p>But if you have to satisfy another lib's dependency with you can't get away that easily.</p>

<p>LibLoader works around this limitation. You provide your lib with the version stripped from the file name.
In this way you can still utilize Android mechanism for choosing the right lib for the right architecture. You
then load the lib via LibLoader by passing it the naked libname and the version, e.g. LibLoader.load("foo", "1").</p>

<p>Keep in mind: A true solution can only be brought with future Android versions.</p>

@author Felix Homann <linuxaudio@showlabor.de>
