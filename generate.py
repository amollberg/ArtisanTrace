#!/usr/bin/env python3

from tkinter import *

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

  def xy(event):
    global lastx, lasty
    lastx, lasty = event.x, event.y
  canvas.bind("<Button-1>", xy)

  def addLine(event):
    canvas.create_line(lastx, lasty, event.x, event.y, fill='red', width=3)
    xy(event)
  canvas.bind("<B1-Motion>", addLine)

  canvas.pack()
  root.mainloop()

if __name__ == '__main__':
  main()
