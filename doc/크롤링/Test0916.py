import requests
from bs4 import BeautifulSoup
import re
import pandas as pd
import json

# 크롤링 함수
def Crawler(query, s_date, e_date, page):
    s_from = s_date.replace(".", "")
    e_to = e_date.replace(".", "")

    title_text = []
    link_text = []
    contents_text = []
    date_text = []
    idx = "1"
    pages = int(page)*10

    while int(idx) < pages :
        url = "https://search.naver.com/search.naver?where=news&sm=tab_pge&query="+query+"&sort=1&photo=0&field=0&pd=3&ds="+s_date+"&de="+e_date+"&mynews=0&office_type=0&office_section_code=0&news_office_checked=&nso=so:dd,p:from"+s_from+"to"+e_to+",a:all&start="+idx
        req = requests.get(url, headers={'User-Agent' : 'Mozilla/5.0'})
        soup = BeautifulSoup(req.text, 'html.parser')

        # 제목, url 받아오기
        atags = soup.find_all('a', 'news_tit')
        for atag in atags:
            title = atag.get('title')
            title_text.append(title)
            link_text.append(atag['href'])

        # 본문 받아오기
        contents_lists = soup.find_all('a', 'api_txt_lines dsc_txt_wrap')
        for contents_list in contents_lists:
            contents_text.append(contents_list.text)

        # 날짜 받아오기
        spans = soup.find_all('span', 'info')
        for span in spans:
            p1 = re.compile(r'\d{4}.\d{2}.\d{2}.')
            p2 = re.compile(r'전$')
            m1 = p1.search(span.text)
            m2 = p2.search(span.text)
            if m1 or m2 :
                date_text.append(span.text)
            else :
                continue

        idx = str(int(idx) + 10)
    
    print(len(title_text))
    print(len(link_text))
    print(len(contents_text))
    print(len(date_text))

    # 데이터 출력(임시)
    #for i in range(len(result)):
    #    print(result[i])
    #    print("\n")

    #SaveCSV(title_text, link_text, contents_text, date_text)
    toJson(title_text, link_text, contents_text, date_text)


# CSV 파일 저장
def SaveCSV(title, link, contents, date) :
    result_csv = []
    for i in range(len(title)):
        result_csv.append([title[i], link[i], contents[i], date[i]])
    dataframe = pd.DataFrame(result_csv, columns=['title', 'url', 'content', 'date'])
    dataframe.to_csv("crawl.csv")

# JSON 형식 저장
def toJson(title, link, contents, date) :
    #result_json = {}
    #for i in range(len(title)):
        #result_json[i] = {"title" : title[i], "link" : link[i], "content" : contents[i], "date" : date[i]} 
    #print(result_json)

    data = {}
    data['posts'] = []
    for i in range(len(title)):
        data['posts'].append({
            "title" : title[i],
            "link" : link[i],
            "contents" : contents[i],
            "date" : date[i]
        })
    print(json.dumps(data, indent=4, ensure_ascii=False))
    SaveJson(data)

# JSON 파일 저장
def SaveJson(data) :
    with open('sample.json', 'w', encoding='utf-8-sig') as json_file:
        json.dump(data, json_file, indent=4, ensure_ascii=False)


Crawler("이스트소프트", "2021.09.16", "2020.09.16", "2")