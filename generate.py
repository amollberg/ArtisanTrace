#!/usr/bin/env python3

from tkinter import *

class Line():
  def __init__(self):
    self.start_x = -1
    self.start_y = -1
    self.end_x = -1
    self.end_y = -1

  def coords(self):
    return (self.start_x, self.start_y, self.end_x, self.end_y)

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
    draw()

  canvas.bind("<Button-1>", start_drag)
  canvas.bind("<B1-Motion>", drag)

  canvas.pack()
  root.mainloop()

if __name__ == '__main__':
  main()
