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
  def __init__(self):
    self.start_x = -1
    self.start_y = -1
    self.end_x = -1
    self.end_y = -1

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
    if vec.y/vec.x < math.tan(45/2 * math.pi/180):
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

lastx, lasty = 0, 0
def main():
  root = Tk()
  app = Window(root)
  root.wm_title("Tkinter window test")

  canvas = Canvas(root, bg="black", height=600, width=600)
  dragline = canvas.create_line((-1, -1, -1, -1), fill='red', width=3)

  dragline_coords = Line()

  def draw():
    canvas.coords(dragline, dragline_coords.coords())

  def start_drag(event):
    dragline_coords.start_x = event.x
    dragline_coords.start_y = event.y

  def drag(event):
    dragline_coords.end_x = event.x
    dragline_coords.end_y = event.y
    dragline_coords.snap_to_45()
    draw()

  canvas.bind("<Button-1>", start_drag)
  canvas.bind("<B1-Motion>", drag)

  canvas.pack()
  root.mainloop()

if __name__ == '__main__':
  main()
