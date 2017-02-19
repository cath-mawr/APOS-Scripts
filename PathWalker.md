PathWalker
==========

PathWalker is an API which became commonplace in APOS scripts written
by me and a few others. It greatly simplifies writing scripts that
walk to specific locations.

It can (mostly) handle common obstacles such as doors.

There's a built-in script which can take you to the town or tile of
your choice.

Quirks
------
* A path can't start or end directly next to a boundary (wall).
* Paths must begin and end on the same level - two paths must
be used if you want to go upstairs or downstairs, with a break
in-between to use the teleportation object.
* Big fences cause problems: the inside of the fence appears to
be reachable, so there's no attempt to get around it through a
door. This problem exists at the Falador members gate and Grand
Tree. Could be fixed by hardcoding various rectangles and
overriding isReachable to see their insides as unreachable when
outside, and their outsides unreachable when inside.
* Calculation could be faster. Could be fixed by using a jump
point search, but that would sacrifice some route preference.

API
---

~~~
final class PathWalker.Location {

    final String name;
    final int x;
    final int y;
    final boolean bank;

    PathWalker.Location(String name, int x, int y, boolean bank)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        this.bank = bank;
    }
}

static PathWalker.Location[] locations;
    This is initialized with major cities' banks by default.

PathWalker.Location getNearestBank(int x, int y);

PathWalker(Extension ex);

void init(null);
    Initialize the PathWalker.

void resetWait();
    Walk immediately when walkPath() is next called. This is supposed
    to be used when the path is interrupted, for example, when combat
    is entered and the script needs to run away.

PathWalker.Path calcPath(int x, int y);
    Calculate a path from your current location.

PathWalker.Path calcPath(int x1, int y1, int x2, int y2);
    Calculate a path from the origin to the destination.

void setPath(PathWalker.Path p);

boolean walkPath();
    Walk to the next point on the set path. This returns false while
    the destination has not been reached.
    If it's not time to walk to the next point, this does nothing, and
    returns false. Calculation of when "the time is right" is mostly
    automatic; see resetWait().
~~~
