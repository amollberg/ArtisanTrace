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
  return ((deti * A, deti * D, deti * G),
          (deti * B, deti * E, deti * H),
          (deti * C, deti * F, deti * I))
def test_invert3():
  pass
test_invert3()

def minus(vec1, vec2):
  return Vec(vec1.x - vec2.x, vec1.y - vec2.y)


def unfold(vec, fold_matrix):
  return fold(vec, invert(fold_matrix))

def remove_tag(canvas, item, tag_to_remove):
  tags = canvas.gettags(item)
  canvas.itemconfig(item, tags=(tag for tag in tags
                                if tag != tag_to_remove))

class AbstractLine:

  def coords(self):
    raise NotImplementedError

  def start(self):
    return (self.start_x(), self.start_y())

  def end(self):
    return (self.end_x(), self.end_y())

  def start_x(self):
    start_x, _, _, _ = self.coords()
    return start_x

  def start_y(self):
    _, start_y, _, _ = self.coords()
    return start_y

  def end_x(self):
    _, _, end_x, _ = self.coords()
    return end_x

  def end_y(self):
    _, _, _, end_y = self.coords()
    return end_y

  def set_coords(self, coords):
    raise NotImplementedError

  def set_start(self, xy):
    _, _, end_x, end_y = self.coords()
    start_x, start_y = xy
    self.set_coords((start_x, start_y, end_x, end_y))

  def set_end(self, xy):
    start_x, start_y, _, _ = self.coords()
    end_x, end_y = xy
    self.set_coords((start_x, start_y, end_x, end_y))

  def snap_to_45(self):
    """ Make the end the nearest point that also makes the line
        slope a multiple of 45 degrees
    """
    start_x, start_y, end_x, end_y = self.coords()
    vec = Vec(end_x - start_x,
              end_y - start_y)
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
    end_x = start_x + vec.x
    end_y = start_y + vec.y
    self.set_coords((start_x, start_y, end_x, end_y))

  def __repr__(self):
    x1, y1, x2, y2 = self.coords()
    class_name = type(self).__name__
    return f"<{class_name} ({x1}, {y1}) to ({x2}, {y2})>"

class FreeLine(AbstractLine):
  def __init__(self, start_x = -1, start_y = -1, end_x = -1, end_y = -1):
    self._coords = (start_x, start_y, end_x, end_y)

  def coords(self):
    return self._coords

  def set_coords(self, coords):
    x1, y1, x2, y2 = coords
    self._coords = (x1, y1, x2, y2)

class CanvasLine(AbstractLine):
  def __init__(self, canvas, *create_line_args, **create_line_kwargs):
    self.canvas = canvas
    self.handle = self.canvas.create_line(*create_line_args, **create_line_kwargs)

  def coords(self):
    return self.canvas.coords(self.handle)

  def set_coords(self, coords):
    self.canvas.coords(self.handle, coords)

def test_line():
  line = FreeLine()
  line.set_start((0, 0))
  line.set_end((3, 100))
  line.snap_to_45()
  print(line)
  assert(line.end_x() == 0)
  assert(line.end_y() == 100)
test_line()

class CanvasOval:
  def __init__(self, canvas, *create_oval_args, **create_oval_kwargs):
    self.canvas = canvas
    self.handle = self.canvas.create_oval(*create_oval_args, **create_oval_kwargs)

  def coords(self):
    return self.canvas.coords(self.handle)

  def set_coords(self, coords):
    self.canvas.coords(self.handle, coords)

  def move_to(self, new_center_xy):
    tl_x, tl_y, br_x, br_y = self.canvas.bbox(self.handle)
    c_x, c_y = (tl_x + br_x)/2, (tl_y + br_y)/2
    nc_x, nc_y = new_center_xy
    delta_x = nc_x - c_x
    delta_y = nc_y - c_y
    tl_x += delta_x
    tl_y += delta_y
    br_x += delta_x
    br_y += delta_y
    self.set_coords((tl_x, tl_y, br_x, br_y))

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

  def key_release(self, _):
    pass

class Trace:
  def __init__(self, line1, line2):
    self.line1 = line1
    self.line2 = line2
    self.angle = Angle.OBTUSE
    self.knee = 0

  def _recalculate(self):
    """ Calculate the location of the kneepoint given
        the start, end, direction, and corner angle
    """
    trace = Vec(self.line2.end_x() - self.line1.start_x(),
                self.line2.end_y() - self.line1.start_y())
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
    kx, ky = self.line1.start_x() + kneepoint.x, self.line1.start_y() + kneepoint.y
    self.line1.set_end((kx, ky))
    self.line2.set_start((kx, ky))

  def is_initialized(self):
    return (self.line1.start_x, self.line1.start_y) != (-1, -1)

  def get_kneepoint(self):
    return self.line1.end()

  def set_start(self, xy):
    self.line1.set_start(xy)
    self._recalculate()

  def set_end(self, xy):
    self.line2.set_end(xy)
    self._recalculate()

  def set_angle(self, angle):
    self.angle = angle
    self._recalculate()

def test_trace():
  t = Trace(FreeLine(), FreeLine())
  t.set_start((0,0))
  t.set_end((20, 10))
  assert((10, 0) == t.get_kneepoint())

  t = Trace(FreeLine(), FreeLine())
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
    self.trace = Trace(CanvasLine(self.canvas, (-1, -1, -1, -1), fill='red', width=3),
                       CanvasLine(self.canvas, (-1, -1, -1, -1), fill='red', width=3))
    self.state = TraceDrawTool.STATE_INACTIVE
    self.do_snap = True

  def start_drag(self, _):
    def add(line_handle):
      self.viewmodel.lines.append(line_handle)
      # Move to bottom of z-stack
      self.canvas.lower(self.viewmodel.lines[-1])
    if self.trace.is_initialized():
      add(self.canvas.create_line(self.trace.line1.coords(), fill='red', width=3))
      add(self.canvas.create_line(self.trace.line2.coords(), fill='red', width=3))

    for handle in self.canvas.find_withtag("hover"):
      if handle in self.viewmodel.pads:
        self.canvas.itemconfig(handle, outline='yellow')
        tl_x, tl_y, br_x, br_y = self.canvas.bbox(handle)
        c_x, c_y = (tl_x + br_x)/2, (tl_y + br_y)/2
        self.trace.set_start((c_x, c_y))
        return
    self.trace.set_start(self.viewmodel.cursor)

  def rightclick(self, event):
    x, y = event.x, event.y
    self.canvas.addtag_closest("rightclick", x, y)

  def mouse_move(self, _):
    if self.do_snap:
      for handle in self.viewmodel.pads:
        if handle in self.canvas.find_withtag("hover"):
          self.canvas.itemconfig(handle, outline='yellow')
          tl_x, tl_y, br_x, br_y = self.canvas.bbox(handle)
          c_x, c_y = (tl_x + br_x)/2, (tl_y + br_y)/2
          self.trace.set_end((c_x, c_y))
        else:
          self.canvas.itemconfig(handle, outline='red')
    else:
      self.trace.set_end(self.viewmodel.cursor)

  def key_release(self, event):
    if event.keysym in ("Control_L", "Control_R"):
      self.do_snap = True
      self.mouse_move(None)


  def deactivate(self):
    self.trace.line1.set_coords((-1, -1, -1, -1))
    self.trace.line2.set_coords((-1, -1, -1, -1))

class Interface:
  def __init__(self, line):
     self.line = line

  def point1(self):
    return (self.line.start_x, self.line.start_y)

  def set_point1(self, xy):
    x, y = xy
    self.line.start_x = x
    self.line.start_y = y

  def set_point2(self, xy):
    x, y = xy
    self.line.end_x = x
    self.line.end_y = y

  def get_position(self):
    x1, y1, x2, y2 = self.line.coords()
    return (x1 + x2)/2, (y1 + y2)/2

  def move(self, xy):
    px, py = self.get_position()
    new_px, new_py = xy
    dx, dy = new_px - px, new_py - py
    self.line.start_x += dx
    self.line.start_y += dy
    self.line.end_x += dx
    self.line.end_y += dy

class InterfaceDrawTool(EmptyTool):
  STATE_TOPLACE = 0
  STATE_PLACED1 = 1

  def __init__(self, viewmodel):
    self.viewmodel = viewmodel
    self.canvas = self.viewmodel.canvas
    self.cursor_pad = CanvasOval(self.canvas, (-1, -1, -1, -1), outline='red', fill='black')
    self.cursor_line = CanvasLine(self.canvas, (-1, -1, -1, -1), fill='red', width=1)
    self.interface = Interface(self.cursor_line)
    self.state = InterfaceDrawTool.STATE_TOPLACE

  def draw(self):
    self.canvas.coords(self.cursor_pad.coords(), self.viewmodel.cursor)
    if self.state == InterfaceDrawTool.STATE_TOPLACE:
      self.canvas.coords(self.cursor_line, (-1, -1, -1, -1))
    elif self.state == InterfaceDrawTool.STATE_PLACED1:
      self.canvas.coords(self.cursor_line, self.interface.line.coords())

  def deactivate(self):
    self.cursor_line.set_coords((-1, -1, -1, -1))
    self.state = InterfaceDrawTool.STATE_TOPLACE

  def start_drag(self, event):
    x, y = event.x, event.y
    if self.state == InterfaceDrawTool.STATE_TOPLACE:
      self.interface.set_point1(self.viewmodel.cursor)
      self.cursor_pad = CanvasOval(self.canvas, (x-2, y-2, x+2, y+2), outline='red', fill='black')
      # Move to top of z-stack
      self.canvas.lift(self.cursor_pad.handle)
      self.state = InterfaceDrawTool.STATE_PLACED1
    elif self.state == InterfaceDrawTool.STATE_PLACED1:
      self.state = InterfaceDrawTool.STATE_TOPLACE

  def dragging(self, _):
    self.mouse_move(None)

  def mouse_move(self, _):
    if self.state == InterfaceDrawTool.STATE_TOPLACE:
      self.interface.set_point1(self.viewmodel.cursor)
      print(self.interface.line.coords())
    elif self.state == InterfaceDrawTool.STATE_PLACED1:
      cx, cy = self.viewmodel.cursor
      self.interface.line.set_end((cx, cy))
      self.interface.line.snap_to_45()
      self.interface.set_point2((self.interface.line.end_x, self.interface.line.end_y))

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
    self.interfaces = []
    self.is_dragging = False
    self.drag = FreeLine()
    self.emptytool = EmptyTool()
    self.tracedrawtool = TraceDrawTool(self)
    self.paddrawtool = PadDrawTool(self)
    self.interfacedrawtool = InterfaceDrawTool(self)
    self.tool = self.paddrawtool
    self.cursor = (-1, -1)
    canvas.bind("<Button-1>", self.start_drag)
    canvas.bind("<B1-Motion>", self.dragging)
    canvas.bind("<ButtonRelease-1>", self.release)
    canvas.bind("<Button-3>", self.rightclick)
    canvas.bind("<Motion>", self.mouse_move)
    canvas.bind("<Key>", self.key_press)
    canvas.bind("<KeyRelease>", self.key_release)
    canvas.bind("q", self.tool_exit)
    canvas.bind("w", self.tool_draw_trace)
    canvas.bind("e", self.tool_draw_pad)
    canvas.bind("r", self.tool_draw_interface)
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

  def key_release(self, event):
    self.tool.key_release(event)
    self.draw()

  def change_tool(self, new_tool):
    self.tool.deactivate()
    self.tool = new_tool

  def tool_draw_trace(self, _):
    self.change_tool(self.tracedrawtool)

  def tool_draw_pad(self, _):
    self.change_tool(self.paddrawtool)

  def tool_draw_interface(self, _):
    self.change_tool(self.interfacedrawtool)

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
