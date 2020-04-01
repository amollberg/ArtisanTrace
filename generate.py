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

class ViewModel:
  def __init__(self, canvas):
    self.canvas = canvas
    self.drag = Line(-1, -1, -1, -1)
    self.dragline = canvas.create_line(self.drag.coords(), fill='red', width=3)
    self.is_dragging = False
    self.lines = []
    canvas.bind("<Button-1>", self.start_drag)
    canvas.bind("<B1-Motion>", self.dragging)
    canvas.bind("<ButtonRelease-1>", self.release)
    canvas.bind("<Button-3>", self.rightclick)
    canvas.pack()

  def draw(self):
    self.canvas.coords(self.dragline,
                       self.drag.coords() if self.is_dragging
                       else Line(-1, -1, -1, -1).coords())
    for handle in self.canvas.find_withtag("rightclick"):
      self.canvas.itemconfig(handle, fill='yellow')

  def start_drag(self, event):
    self.drag.start_x = event.x
    self.drag.start_y = event.y

  def dragging(self, event):
    self.is_dragging = True
    self.drag.end_x = event.x
    self.drag.end_y = event.y
    self.drag.snap_to_45()
    self.draw()

  def release(self, _):
    self.is_dragging = False
    self.lines.append(self.canvas.create_line(self.drag.coords(), fill='red', width=3))
    print(self.lines)
    self.draw()

  def rightclick(self, event):
    x, y = event.x, event.y
    self.canvas.addtag_closest("rightclick", x, y)
    self.draw()


lastx, lasty = 0, 0
def main():
  root = Tk()
  app = Window(root)
  root.wm_title("Tkinter window test")

  canvas = Canvas(root, bg="black", height=600, width=600)
  viewmodel = ViewModel(canvas)
  root.mainloop()

if __name__ == '__main__':
  main()
