from selenium import webdriver
from selenium.webdriver.common.keys import Keys

driver = webdriver.Firefox()
driver.get("https://www.felixcote.fr/about-test.html")
driver.fullscreen_window()