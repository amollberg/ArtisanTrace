#!/usr/bin/env python3

from tkinter import *
from collections import namedtuple
import math
import cmath

class Direction:
  N = 0
  NE = N + 45
  E = NE + 45
  SE = E + 45
  S = SE + 45
  SW = S + 45
  W = SW + 45
  NW = W + 45

class Angle:
  ACUTE = 0
  RIGHT = 1
  OBTUSE = 2

class Vec(namedtuple('Vec', 'x y')):
  pass

def arg(xy):
  """ Return the counter-clockwise angle from positive x-axis to xy in degrees, -180 to 180
  """
  x, y = xy
  z = x + y*1j
  phi_rad = cmath.phase(z)
  return phi_rad * 180/math.pi
def test_arg():
  assert(0 == arg((1, 0)))
  assert(45 == arg((1, 1)))
  assert(-135 ==round(arg((-1, -1))))
test_arg()

def fold(vec, fold_matrix):
  x, y = vec
  (fold_xx, fold_xy), (fold_yx, fold_yy) = fold_matrix
  return Vec(x * fold_xx + y * fold_yx,
             x * fold_xy + y * fold_yy)

def invert(matrix2x2):
  (a, b), (c, d) = matrix2x2
  det = (a*d - b*c)
  if det == 0:
    return None
  deti = 1/det
  return ((deti * d, -deti * b), (-deti * c, deti * a))

def invert3(matrix3x3):
  (a, b, c), (d, e, f), (g, h, i) = matrix3x3
  det = a*(e*i + f*h) - b*(d*i - f*g) + c*(d*h - e*g)
  if det == 0:
    return None
  deti = 1/det
  A = e*i-f*h
  B = -(d*i-f*g)
  C = d*h - e*g
  D = -(b*i-c*h)
  E = a*i-c*g
  F = -(a*h-b*g)
  G = b*f-c*e
  H = -(a*f-c*d)
  I = a*e-b*d
  return ((A, D, G),
          (B, E, H),
          (C, F, I))

def minus(vec1, vec2):
  return Vec(vec1.x - vec2.x, vec1.y - vec2.y)


def unfold(vec, fold_matrix):
  return fold(vec, invert(fold_matrix))

def remove_tag(canvas, item, tag_to_remove):
  tags = canvas.gettags(item)
  canvas.itemconfig(item, tags=(tag for tag in tags
                                if tag != tag_to_remove))

class Line():
  def __init__(self, start_x = -1, start_y = -1, end_x = -1, end_y = -1):
    self.start_x = start_x
    self.start_y = start_y
    self.end_x = end_x
    self.end_y = end_y

  def coords(self):
    return (self.start_x, self.start_y, self.end_x, self.end_y)

  def snap_to_45(self):
    """ Make the end the nearest point that also makes the line
        slope a multiple of 45 degrees
    """
    vec = Vec(self.end_x - self.start_x,
              self.end_y - self.start_y)
    foldings = []
    if vec.x < 0:
      foldings.append(((-1, 0),(0, 1)))
      vec = fold(vec, foldings[-1])
    if vec.y < 0:
      foldings.append(((1, 0),(0, -1)))
      vec = fold(vec, foldings[-1])
    if vec.x < vec.y:
      foldings.append(((0, 1), (1, 0)))
      vec = fold(vec, foldings[-1])
    # Perform snapping
    if vec.x == 0:
      pass
    elif vec.y/vec.x < math.tan(45/2 * math.pi/180):
      # Project to x-axis
      vec = Vec(vec.x, 0)
    else:
      # Project to diagonal
      c = (vec.x + vec.y)/2
      vec = Vec(c, c)

    # Undo folding
    for folding in reversed(foldings):
      vec = unfold(vec, folding)
    self.end_x = self.start_x + vec.x
    self.end_y = self.start_y + vec.y

  def __repr__(self):
    return f"<Line ({self.start_x}, {self.start_y}) to ({self.end_x}, {self.end_y})>"

def test_line():
  line = Line()
  line.start_x = 0
  line.start_y = 0
  line.end_x = 3
  line.end_y = 100
  line.snap_to_45()
  assert(line.end_x == 0)
  assert(line.end_y == 100)
test_line()

class Window(Frame):
  def __init__(self, master=None):
    Frame.__init__(self, master)
    self.master = master

class EmptyTool:
  def draw(self):
    pass

  def deactivate(self):
    pass

  def start_drag(self, _):
    pass

  def dragging(self, _):
    pass

  def release(self, _):
    pass

  def rightclick(self, _):
    pass

  def mouse_move(self, _):
    pass

  def key_press(self, _):
    pass

class Trace:
  def __init__(self):
    self.line1 = Line(-1, -1, -1, -1)
    self.line2 = Line(-1, -1, -1, -1)
    self.angle = Angle.OBTUSE
    self.knee = 0

  def _recalculate(self):
    """ Calculate the location of the kneepoint given
        the start, end, direction, and corner angle
    """
    trace = Vec(self.line2.end_x - self.line1.start_x,
                self.line2.end_y - self.line1.start_y)
    x, y = trace.x, trace.y
    kneepoints = [
      Vec(x-y, 0),
      Vec(x, 0),
      Vec(x+y, 0),

      Vec(0, y-x),
      Vec(0, y),
      Vec(0, y+x),

      Vec(-y, y),
      Vec((x-y)/2, (y-x)/2),
      Vec(x, -x),

      Vec(y, y),
      Vec((x+y)/2, (x+y)/2),
      Vec(x, x),
    ]
    def angle_of(k):
      origin = Vec(0,0)
      a1 = arg(minus(origin, k))
      a2 = arg(minus(trace, k))
      return (a1 - a2) % 360
    # TODO: Pick the most suitable knee first
    for k in kneepoints:
      kneepoint = k
      a = 180 - abs(angle_of(k) - 180)
      if self.angle == Angle.OBTUSE:
        if a > 90:
          break
      if self.angle == Angle.RIGHT:
        if a == 90:
          break
      if self.angle == Angle.ACUTE:
        if a < 90:
          break
    self.line2.start_x = self.line1.end_x = self.line1.start_x + kneepoint.x
    self.line2.start_y = self.line1.end_y = self.line1.start_y + kneepoint.y


  def get_kneepoint(self):
    return self.line1.end_x, self.line1.end_y

  def set_start(self, xy):
    x, y = xy
    self.line1.start_x = x
    self.line1.start_y = y
    self._recalculate()

  def set_end(self, xy):
    x, y = xy
    self.line2.end_x = x
    self.line2.end_y = y
    self._recalculate()

  def set_angle(self, angle):
    self.angle = angle
    self._recalculate()

def test_trace():
  t = Trace()
  t.set_start((0,0))
  t.set_end((20, 10))
  assert((10, 0) == t.get_kneepoint())

  t = Trace()
  t.set_start((10, 5))
  t.set_end((110, 205))
  t.set_angle(Angle.OBTUSE)
  assert((10, 105) == t.get_kneepoint())
test_trace()


class TraceDrawTool(EmptyTool):
  STATE_INACTIVE = 0
  STATE_STARTED = 1

  def __init__(self, viewmodel):
    self.viewmodel = viewmodel
    self.canvas = self.viewmodel.canvas
    self.line1 = self.canvas.create_line((-1, -1, -1, -1), fill='red', width=3)
    self.line2 = self.canvas.create_line((-1, -1, -1, -1), fill='red', width=3)
    self.trace = Trace()
    self.state = TraceDrawTool.STATE_INACTIVE

  def draw(self):
    self.canvas.coords(self.line1, self.trace.line1.coords())
    self.canvas.coords(self.line2, self.trace.line2.coords())

  def start_drag(self, _):
    for handle in self.canvas.find_withtag("hover"):
      if handle in self.viewmodel.pads:
        self.canvas.itemconfig(handle, outline='yellow')
        tl_x, tl_y, br_x, br_y = self.canvas.bbox(handle)
        c_x, c_y = (tl_x + br_x)/2, (tl_y + br_y)/2
        self.trace.set_start((c_x, c_y))

  def dragging(self, _):
    pass

  def release(self, _):
    def add(line_handle):
      self.viewmodel.lines.append(line_handle)
      # Move to bottom of z-stack
      self.canvas.lower(self.viewmodel.lines[-1])
    add(self.canvas.create_line(self.trace.line1.coords(), fill='red', width=3))
    add(self.canvas.create_line(self.trace.line2.coords(), fill='red', width=3))

  def rightclick(self, event):
    x, y = event.x, event.y
    self.canvas.addtag_closest("rightclick", x, y)

  def mouse_move(self, _):
    for handle in self.viewmodel.pads:
      if handle in self.canvas.find_withtag("hover"):
        self.canvas.itemconfig(handle, outline='yellow')
      else:
        self.canvas.itemconfig(handle, outline='red')
    self.trace.set_end(self.viewmodel.cursor)

  def key_press(self, event):
    if event.char == " ":
      if self.viewmodel.is_dragging:
        self.release(None)
        self.start_drag(None)
        # Set the start of the new drag to the end of the newly created line
        new_line = Line(*self.canvas.coords(self.viewmodel.lines[-1]))
        self.viewmodel.drag.start_x = new_line.end_x
        self.viewmodel.drag.start_y = new_line.end_y

class PadDrawTool(EmptyTool):
  def __init__(self, viewmodel):
    self.viewmodel = viewmodel
    self.canvas = self.viewmodel.canvas
    self.cursorpad = self.canvas.create_oval((-1, -1, -1, -1), outline='yellow', fill='black')

  def deactivate(self):
    self.canvas.coords(self.cursorpad,
                       (-1, -1, -1, -1))

  def start_drag(self, event):
    x, y = event.x, event.y
    self.viewmodel.pads.append(
      self.canvas.create_oval((x-5, y-5, x+5, y+5), outline='red', fill='black'))
    # Move to top of z-stack
    self.canvas.lift(self.viewmodel.pads[-1])

  def dragging(self, _):
    self.mouse_move(None)

  def mouse_move(self, _):
    x, y = self.viewmodel.cursor
    self.canvas.coords(self.cursorpad,
                       (x-5, y-5, x+5, y+5))

class ViewModel:
  def __init__(self, canvas):
    self.canvas = canvas
    self.lines = []
    self.pads = []
    self.is_dragging = False
    self.drag = Line(-1, -1, -1, -1)
    self.emptytool = EmptyTool()
    self.tracedrawtool = TraceDrawTool(self)
    self.paddrawtool = PadDrawTool(self)
    self.tool = self.tracedrawtool
    self.cursor = (-1, -1)
    canvas.bind("<Button-1>", self.start_drag)
    canvas.bind("<B1-Motion>", self.dragging)
    canvas.bind("<ButtonRelease-1>", self.release)
    canvas.bind("<Button-3>", self.rightclick)
    canvas.bind("<Motion>", self.mouse_move)
    canvas.bind("<Key>", self.key_press)
    canvas.bind("q", self.tool_exit)
    canvas.bind("w", self.tool_draw_trace)
    canvas.bind("e", self.tool_draw_pad)
    canvas.pack()

  def draw(self):
    self.tool.draw()
    for handle in self.canvas.find_withtag("rightclick"):
      self.canvas.itemconfig(handle, fill='yellow')

  def start_drag(self, event):
    self.drag.start_x = event.x
    self.drag.start_y = event.y
    self.tool.start_drag(event)
    self.draw()

  def dragging(self, event):
    self.is_dragging = True
    self.cursor = (event.x, event.y)
    self.drag.end_x = event.x
    self.drag.end_y = event.y
    self.tool.dragging(event)
    self.draw()

  def release(self, event):
    self.is_dragging = False
    self.tool.release(event)
    self.draw()

  def rightclick(self, event):
    self.tool.rightclick(event)
    self.draw()

  def mouse_move(self, event):
    x, y = event.x, event.y
    remove_tag(self.canvas, "hover", "hover")
    self.canvas.addtag_closest("hover", x, y)
    self.cursor = (event.x, event.y)
    self.tool.mouse_move(event)
    self.draw()

  def key_press(self, event):
    self.tool.key_press(event)
    self.draw()

  def change_tool(self, new_tool):
    self.tool.deactivate()
    self.tool = new_tool

  def tool_draw_trace(self, _):
    self.change_tool(self.tracedrawtool)

  def tool_draw_pad(self, _):
    self.change_tool(self.paddrawtool)

  def tool_exit(self, _):
    self.change_tool(self.emptytool)

lastx, lasty = 0, 0
def main():
  root = Tk()
  app = Window(root)
  root.wm_title("Tkinter window test")

  canvas = Canvas(root, bg="black", height=600, width=600)
  viewmodel = ViewModel(canvas)
  # Set keyboard focus to canvas
  canvas.focus_set()
  root.mainloop()

if __name__ == '__main__':
  main()
