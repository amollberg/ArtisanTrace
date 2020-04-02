#!/usr/bin/env python3

from tkinter import *
from collections import namedtuple
import math

class Vec(namedtuple('Vec', 'x y')):
  pass

def fold(vec, fold_matrix):
  x, y = vec
  (fold_xx, fold_xy), (fold_yx, fold_yy) = fold_matrix
  return Vec(x * fold_xx + y * fold_yx,
             x * fold_xy + y * fold_yy)

def invert(matrix2x2):
  (a, b), (c, d) = matrix2x2
  det = 1/(a*d - b*c)
  return ((det * d, -det * b), (-det * c, det * a))

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

class TraceDrawTool(EmptyTool):
  def __init__(self, viewmodel):
    self.viewmodel = viewmodel
    self.canvas = self.viewmodel.canvas
    self.dragline = self.canvas.create_line((-1, -1, -1, -1), fill='red', width=3)

  def draw(self):
    self.canvas.coords(self.dragline,
                       self.viewmodel.drag.coords() if self.viewmodel.is_dragging
                       else Line(-1, -1, -1, -1).coords())

  def start_drag(self, _):
    for handle in self.canvas.find_withtag("hover"):
      self.canvas.itemconfig(handle, fill='yellow')
      tl_x, tl_y, br_x, br_y = self.canvas.bbox(handle)
      c_x, c_y = (tl_x + br_x)/2, (tl_y + br_y)/2
      self.viewmodel.drag.start_x = c_x
      self.viewmodel.drag.start_y = c_y

  def dragging(self, _):
    self.viewmodel.drag.snap_to_45()

  def release(self, _):
    self.viewmodel.lines.append(
      self.canvas.create_line(self.viewmodel.drag.coords(), fill='red', width=3))
    # Move to bottom of z-stack
    self.canvas.lower(self.viewmodel.lines[-1])

  def rightclick(self, event):
    x, y = event.x, event.y
    self.canvas.addtag_closest("rightclick", x, y)

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
