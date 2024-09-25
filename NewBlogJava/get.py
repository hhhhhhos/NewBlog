from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
from bs4 import BeautifulSoup
import sys

def scrape_data_original_with_selenium(url):
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    chrome_options.add_argument("--disable-gpu")
    chrome_options.add_argument("--no-sandbox")
    
    service = Service('G:\\chromedriver.exe')
    driver = webdriver.Chrome(service=service, options=chrome_options)
    
    try:
        driver.get(url)
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.TAG_NAME, "body"))
        )
        page_source = driver.page_source
        soup = BeautifulSoup(page_source, 'html.parser')
        
        # Use a list instead of a set to maintain order
        data_original_list = []
        elements = soup.find_all(attrs={"data-original": True})
        
        for element in elements:
            data_original = element['data-original']
            if data_original not in data_original_list:
                data_original_list.append(data_original)
        
        return data_original_list
    finally:
        driver.quit()

if __name__ == '__main__':
    url = sys.argv[1]
    data_original_values = scrape_data_original_with_selenium(url)
    
    for index, value in enumerate(data_original_values):
        print(f'{index}\n\n![]({value})\n')