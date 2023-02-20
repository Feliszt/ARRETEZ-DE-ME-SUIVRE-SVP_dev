from selenium import webdriver
from selenium.webdriver.common.keys import Keys

driver = webdriver.Firefox()
driver.get("https://www.felixcote.fr/arretez-de-me-suivre-svp-fullscreen.html")
driver.fullscreen_window()
