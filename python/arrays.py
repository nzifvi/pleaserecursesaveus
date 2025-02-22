import cv2
import numpy as np
import os

def process_image(image_path, width, height):
    img = cv2.imread(image_path)
    img = cv2.resize(img, (width, height))
    blue, green, red = cv2.split(img)
    red_array = red / 255.0
    green_array = green / 255.0
    blue_array = blue / 255.0

    return red_array, green_array, blue_array

def save_rgb_arrays_to_file(red_array, green_array, blue_array, file_path):
    with open(file_path, "w") as f:
        for row in red_array:
            f.write(" ".join(f"{value:.5f}" for value in row) + "\n")
        f.write("n\n")

        for row in green_array:
            f.write(" ".join(f"{value:.5f}" for value in row) + "\n")
        f.write("n\n")

        for row in blue_array:
            f.write(" ".join(f"{value:.5f}" for value in row) + "\n")

image_path = "/Projects/opencv/python/images/Bourbon_biscuit.jpg"
width, height = 500, 500  # Standard dimensions
downloads_folder = os.path.expanduser("~/Downloads")
output_file_path = os.path.join(downloads_folder, "rgb_arrays.txt")
red_array, green_array, blue_array = process_image(image_path, width, height)
save_rgb_arrays_to_file(red_array, green_array, blue_array, output_file_path)

print(f"RGB arrays saved to: {output_file_path}")



#run: using python3 -u "~/arrays.py"
