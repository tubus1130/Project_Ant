import requests
from bs4 import BeautifulSoup
import re
import pandas as pd
import json
from datetime import datetime
import os

# (회사명, 시작날짜, 끝날짜)
def Crawler(company, s_date, e_date) :
    s_from = s_date.replace(".", "")
    e_to = e_date.replace(".", "")

    title_text = []
    link_text = []
    contents_text = []
    date_text = []
    idx = "1"
    go = True

    while go :
        url = "https://search.naver.com/search.naver?where=news&sm=tab_pge&query="+company+"&sort=1&photo=0&field=0&pd=3&ds="+s_date+"&de="+e_date+"&mynews=0&office_type=0&office_section_code=0&news_office_checked=&nso=so:dd,p:from"+s_from+"to"+e_to+",a:all&start="+idx
        req = requests.get(url, headers={'User-Agent' : 'Mozilla/5.0'})
        soup = BeautifulSoup(req.text, 'html.parser')

        # 날짜 받아오기(최신뉴스만 받아오기 수정완료)
        spans = soup.find_all('span', 'info')
        
        for span in spans:
            p1 = re.compile(r'\d{4}.\d{2}.\d{2}.')
            p2 = re.compile(r'일 전$')
            p3 = re.compile(r'시간 전$')
            p4 = re.compile(r'분 전$')
            m1 = p1.search(span.text)
            m2 = p2.search(span.text)
            m3 = p3.search(span.text)
            m4 = p4.search(span.text)
            if m1 or m2 or m3 or m4:
                year = datetime.now().year
                month = datetime.now().month
                day = datetime.now().day
                hour = datetime.now().hour
                minute = datetime.now().minute
                if m2:
                    date_text.append(DayChange(year, month, day, span.text))
                    continue
                if m3:
                    date_text.append(HourChange(year, month, day, hour, span.text))
                    continue
                if m4:
                    date_text.append(MinuteChange(year, month, day, hour, minute, span.text))
                    continue
                date_text.append(span.text)
            else :
                continue

        # 제목, url 받아오기
        atags = soup.find_all('a', 'news_tit')
        for atag in atags:
            if len(title_text) < len(date_text) :
                title = atag.get('title')
                title_text.append(title)
                link_text.append(atag['href'])
            else :
                break

        # 본문 받아오기
        contents_lists = soup.find_all('a', 'api_txt_lines dsc_txt_wrap')
        for contents_list in contents_lists:
            if len(contents_text) < len(date_text) :
                contents_text.append(contents_list.text)
            else :
                break
        
        # span이 empty면 크롤링 멈춤
        if not spans :
            go = False
        else:
            idx = str(int(idx) + 10)
        

    # 오래된순서부터 넣어야되니깐 역순으로해줌
    title_text.reverse()
    link_text.reverse()
    contents_text.reverse()
    date_text.reverse()
    #SaveCSV(title_text, link_text, contents_text, date_text, company)
    toJson(title_text, link_text, contents_text, date_text, company)

# JSON 형식 출력
def toJson(title, link, contents, date, company) :
    data = {}
    data['posts'] = []
    for i in range(len(date)):
        data['posts'].append({
            "title" : title[i],
            "description" : contents[i],
            "newsDate" : date[i],
            "url" : link[i],
            "company" : company
        })
    print(json.dumps(data, indent=4, ensure_ascii=False))

# JSON 파일 저장
def SaveJson(data) :
    with open('sample.json', 'w', encoding='utf-8-sig') as json_file:
        json.dump(data, json_file, indent=4, ensure_ascii=False)

# CSV 파일 저장
def SaveCSV(title, link, contents, date,company) :
    result_csv = []
    for i in range(len(title)):
        result_csv.append([title[i], contents[i], date[i], link[i], company])
    dataframe = pd.DataFrame(result_csv, columns=['title', 'description', 'newsDate', 'url', 'company'])
    if not os.path.exists("crawl.csv"):
        dataframe.to_csv("crawl.csv", mode='w')
    else:
        dataframe.to_csv("crawl.csv", header=False, mode='a')

# -일 전 날짜수정
def DayChange(year, month, day, span) :
    target = span.split('일')[0]
    day -= int(target)
    return MonthChange(year, month, day)

# -시간 전 날짜수정
def HourChange(year, month, day, hour, span) :
    target = span.split('시')[0]
    hour -= int(target)
    if hour <= 0:
        day -= 1
        hour = 24-hour
    return MonthChange(year, month, day)

# -분 전 날짜수정
def MinuteChange(year, month, day, hour, minute, span) :
    target = span.split('분')[0]
    minute -= int(target)
    if minute <= 0:
        hour -= 1
    if hour <= 0:
        day -= 1
        hour = 24-hour
    return MonthChange(year,month,day)

# 날짜 공통수정사항
def MonthChange(year, month, day):

    if day <= 0:
        month -= 1
        if month <= 0:
            year -= 1
            month = 12

        if(month in [1,3,5,7,8,10,12]):
            day = 31 - day
        elif(month in [4,6,9,11]):
            day = 30 - day
        elif(month in [2]):
            if(((year%4==0) and (year%100!=0)) or (year%400==0)):
                day = 29 - day
            else:
                day = 28 - day
    if day < 10:
        day = "0"+str(day)
    else:
        day = str(day)
    if month < 10:
        month = "0"+str(month)
    else:
        month = str(month)
    newdate = str(year) + "." + month + "." + day + "."
    return newdate

Crawler("이스트소프트", "2021.10.18", "2021.10.19")