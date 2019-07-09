//
//  ViewController.swift
//  tidzam
//
//  Created by Lachihab Sabri on 25/01/2018.
//  Copyright © 2018 Lachihab Sabri. All rights reserved.
//

import UIKit
import GoogleMaps
import Foundation
import MapKit
import Charts

class ViewController: UIViewController,GMSMapViewDelegate,UIGestureRecognizerDelegate {
    
    var DeviceName = [Device]()
    var mapview : GMSMapView?
    var DeviceTidmarsh = [Device]()
    var information : [String] = []
    var printinfo : String = ""
    var mp : String = ""
    var viewtidmarsh = UIView()
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title="Welcome to Tidzam"
        navigationController?.navigationBar.isTranslucent = false
        navigationItem.leftBarButtonItem = UIBarButtonItem(image: UIImage(named : "icon-menu"), style: UIBarButtonItemStyle.plain, target: self, action: nil)
        //setup the map
        let camera = GMSCameraPosition.camera(withLatitude: 41.900445,
                                              longitude: -70.5703417,
                                              zoom: 17)
        self.mapview = GMSMapView.map(withFrame: .zero, camera: camera)
        self.mapview?.isMyLocationEnabled=true
        self.mapview?.delegate=self
        self.mapview?.settings.myLocationButton = true
        self.mapview?.mapType = GMSMapViewType.satellite
        self.view = self.mapview
        CallApi()
        listdevicetidmarsh()
    }
    
    func mapView(mapView: GMSMapView, didTapMarker marker: GMSMarker) -> Bool {
        let update = GMSCameraUpdate.zoom(by: 2)
        mapView.animate(with: update)
        return true
    }
    

    
    func mapView(_: GMSMapView, didTap marker: GMSMarker) -> Bool {
        self.mapview?.selectedMarker = marker
        return true
    }
    
    func mapView(_ mapView: GMSMapView, didTapInfoWindowOf marker: GMSMarker) {
        if((marker.title?.range(of: "imp")) != nil)
        {
            let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
            let nextViewController = storyBoard.instantiateViewController(withIdentifier: "SpeciesDeviceID") as! SpeciesDevice
            nextViewController.message=marker.title!
            for i in 0..<DeviceName.count
            {
                if(DeviceName[i].name==marker.title)
                {
                    nextViewController.href=DeviceName[i].href
                }
            }
            show(nextViewController, sender: self)
        }
        else
        {
            let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
            let nextViewController = storyBoard.instantiateViewController(withIdentifier: "TidmarshDeviceID") as! TidmarshDeviceViewController
            show(nextViewController, sender: self)
        }
    }
    
    func mapView(_ mapView: GMSMapView, markerInfoWindow marker: GMSMarker) -> UIView? {
        self.viewtidmarsh.removeFromSuperview()
        var view = UIView(frame: CGRect.init(x: 0, y: 0, width: 0, height: 0))
        if((marker.title?.range(of: "imp")) != nil)
        {
            view = UIView(frame: CGRect.init(x: 0, y: 0, width: 300, height: 200))
            view.backgroundColor = UIColor.white
            view.layer.cornerRadius = 6
            let lbl1 = UILabel(frame: CGRect.init(x: 8, y: 8, width: view.frame.size.width - 16, height: 15))
            let lbl2 = UILabel(frame: CGRect.init(x: lbl1.frame.origin.x, y: lbl1.frame.origin.y + lbl1.frame.size.height + 3, width: view.frame.size.width - 16, height: 15))
            lbl2.text = marker.title
            lbl2.font = UIFont.systemFont(ofSize: 14, weight: .light)
            view.addSubview(lbl2)
            lbl1.text = "Tidzam Device"
            view.addSubview(lbl1)
            let linechart = LineChartView(frame: CGRect.init(x: 5, y: 50, width: 290, height:140 ))
            view.addSubview(linechart)
            var linechartEntry = [ChartDataEntry]()
            var lineorange = [ChartDataEntry]()
            for i in 0..<5
            {
                let value = ChartDataEntry(x: Double(i),y: Double(5*i))
                linechartEntry.append(value)
                let vialue = ChartDataEntry(x: Double(i),y: Double(i))
                lineorange.append(vialue)
            }
            let line1 = LineChartDataSet(values: linechartEntry,label:"BLUE")
            let line2 = LineChartDataSet(values: lineorange,label:"ORANGE")
            line1.colors = [NSUIColor.blue]
            line2.colors = [NSUIColor.orange]
            let data = LineChartData()
            data.addDataSet(line1)
            data.addDataSet(line2)
            linechart.data=data
        }
        else
        {
            self.viewtidmarsh = UIView()
            view = UIView(frame: CGRect.init(x: 0, y: 0, width: 0, height: 0))
            view.backgroundColor = UIColor.white
            for i in 0..<DeviceTidmarsh.count
            {
                if DeviceTidmarsh[i].name.range(of: (marker.title)! ) != nil
                {
                    self.information.removeAll()
                    self.informationsensortidmarsh(href: DeviceTidmarsh[i].href)
                    print(DeviceTidmarsh[i].href)
                }
            }
        }
        return view
    }
    
    @objc func buttonAction(sender: UIButton!) {
        print("LALA")
        
    }
    
    func CallApi() {
        let url = "http://chain-api.media.mit.edu/devices/?limit=3000&site_id=18&offset=0"
        let request = NSMutableURLRequest(url: URL(string: url)!)
        request.httpMethod = "GET"
        
        let requestAPI = URLSession.shared.dataTask(with: request as URLRequest)
        {
            data, response, error in
            if (error != nil) {
                print(error!.localizedDescription) // On indique dans la console ou est le problème dans la requête
            }
            
            
            let responseAPI = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            //print("responseString = \(responseAPI)") // Affiche dans la console la réponse de l'API
            if let jsonObj = try? JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
            {
                //printing the json in console
                //print(jsonObj!.value(forKey: "_links")!)
                if let items = jsonObj!.value(forKey: "_links") as? NSDictionary
                {
                    if let items_ = items.value(forKey: "items") as? NSArray
                    {
                        for item in items_
                        {
                            let device = Device()
                            if let name = item as? NSDictionary
                            {
                                if var name_ = name.value(forKey: "title")
                                {
                                    if((name_ as! String).range(of:"impoundment:") != nil)
                                    {
                                        device.name=name_ as! String
                                        if let href = name.value(forKey: "href")
                                        {
                                            //print(href)
                                            device.href=href as! String
                                        }
                                        self.DeviceName.append(device)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if error == nil {
                // Ce que vous voulez faire.
            }
            print(self.DeviceName.count)
            self.addmarker(devices: self.DeviceName)
        }
        requestAPI.resume()
        //addmarker(devices: self.DeviceName)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func addmarker(devices : [Device]) -> Void {
        for device in devices
        {
            let url = device.href
            let request = NSMutableURLRequest(url: URL(string: url)!)
            request.httpMethod = "GET"
            
            let requestAPI = URLSession.shared.dataTask(with: request as URLRequest)
            {
                data, response, error in
                if (error != nil) {
                    print(error!.localizedDescription) // On indique dans la console ou est le problème dans la requête
                }
                let responseAPI = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
                if let jsonObj = try? JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                {
                    //printing the json in console
                    //print(jsonObj!.value(forKey: "_links")!)
                    if let geo = jsonObj!.value(forKey: "geoLocation") as? NSDictionary
                    {
                        if var lat = geo.value(forKey: "latitude")
                        {
                            if((lat as! Double) != 0.0)
                            {
                                device.latitude=lat as! Double
                                //print(String(device.latitude))
                                if let lng = geo.value(forKey: "longitude")
                                {
                                    print(device.name)
                                    print(String(device.latitude))
                                    device.longitude = lng as! Double
                                    print(" , ")
                                    print(String(device.longitude))
                                    DispatchQueue.main.async(execute: {
                                        let marker = GMSMarker()
                                        marker.position = CLLocationCoordinate2D(latitude: lat as! Double, longitude: lng as! Double)
                                        marker.title = device.name
                                        marker.icon = self.imageWithImage(image: UIImage(named: "ic_micro_maps")!, scaledToSize: CGSize(width: 30.0, height: 30.0))
                                        marker.map = self.mapview
                                    })
                                }
                            }
                        }
                    }
                }
                
            }
            requestAPI.resume()
        }
    }
    
    func imageWithImage(image:UIImage, scaledToSize newSize:CGSize) -> UIImage{
        UIGraphicsBeginImageContextWithOptions(newSize, false, 0.0);
        image.draw(in: CGRect(origin: CGPoint(x: 0,y :0), size: CGSize(width: newSize.width, height: newSize.height))  )
        let newImage:UIImage = UIGraphicsGetImageFromCurrentImageContext()!
        UIGraphicsEndImageContext()
        return newImage
    }
    
    
    func listdevicetidmarsh()
    {
        let url = "http://chain-api.media.mit.edu/devices/?limit=3000&site_id=7&offset=0"
        let request = NSMutableURLRequest(url: URL(string: url)!)
        request.httpMethod = "GET"
        let requestAPI = URLSession.shared.dataTask(with: request as URLRequest)
        {
            data, response, error in
            if (error != nil) {
                print(error!.localizedDescription) // On indique dans la console ou est le problème dans la requête
            }
            
            
            let responseAPI = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            //print("responseString = \(responseAPI)") // Affiche dans la console la réponse de l'API
            if let jsonObj = try? JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
            {
                //printing the json in console
                //print(jsonObj!.value(forKey: "_links")!)
                if let items = jsonObj!.value(forKey: "_links") as? NSDictionary
                {
                    if let items_ = items.value(forKey: "items") as? NSArray
                    {
                        for item in items_
                        {
                            let device = Device()
                            if let name = item as? NSDictionary
                            {
                                if var name_ = name.value(forKey: "title")
                                {
                                    if((name_ as! String).range(of:"0x") != nil)
                                    {
                                        device.name=name_ as! String
                                        if let href = name.value(forKey: "href")
                                        {
                                            //print(href)
                                            device.href=href as! String
                                        }
                                        self.DeviceTidmarsh.append(device)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if error == nil {
                // Ce que vous voulez faire.
            }
            print(self.DeviceTidmarsh.count)
            self.addmarkertidmarsh(devices: self.DeviceTidmarsh)
        }
        requestAPI.resume()
    }
    
    
    func addmarkertidmarsh(devices : [Device]) -> Void {
        for device in devices
        {
            let url = device.href
            let request = NSMutableURLRequest(url: URL(string: url)!)
            request.httpMethod = "GET"
            
            let requestAPI = URLSession.shared.dataTask(with: request as URLRequest)
            {
                data, response, error in
                if (error != nil) {
                    print(error!.localizedDescription) // On indique dans la console ou est le problème dans la requête
                }
                let responseAPI = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
                if let jsonObj = try? JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                {
                    //printing the json in console
                    //print(jsonObj!.value(forKey: "_links")!)
                    if let geo = jsonObj!.value(forKey: "geoLocation") as? NSDictionary
                    {
                        if var lat = geo.value(forKey: "latitude")
                        {
                            if((lat as! Double) != 0.0)
                            {
                                device.latitude=lat as! Double
                                //print(String(device.latitude))
                                if let lng = geo.value(forKey: "longitude")
                                {
                                    print(device.name)
                                    print(String(device.latitude))
                                    device.longitude = lng as! Double
                                    print(" , ")
                                    print(String(device.longitude))
                                    DispatchQueue.main.async(execute: {
                                        let marker = GMSMarker()
                                        marker.position = CLLocationCoordinate2D(latitude: lat as! Double, longitude: lng as! Double)
                                        marker.title = device.name
                                        marker.icon = self.imageWithImage(image: UIImage(named: "icon-info")!, scaledToSize: CGSize(width: 30.0, height: 30.0))
                                        marker.map = self.mapview
                                    })
                                }
                            }
                        }
                    }
                }
            }
            requestAPI.resume()
        }
    }
    
    func informationsensortidmarsh (href : String)
    {
        let idsensors = href.split(separator: "/")
        print(idsensors)
        let url = "http://chain-api.media.mit.edu/sensors/?device_id="+idsensors[idsensors.count-1]
        let request = NSMutableURLRequest(url: URL(string: url)!)
        request.httpMethod = "GET"
        
        let requestAPI = URLSession.shared.dataTask(with: request as URLRequest)
        {
            data, response, error in
            if (error != nil) {
                print(error!.localizedDescription) // On indique dans la console ou est le problème dans la requête
            }
            let responseAPI = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            //print("responseString = \(responseAPI)") // Affiche dans la console la réponse de l'API
            if let jsonObj = try? JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
            {
                //printing the json in console
                //print(jsonObj!.value(forKey: "_links")!)
                if let items = jsonObj!.value(forKey: "_links") as? NSDictionary
                {
                    if let items_ = items.value(forKey: "items") as? NSArray
                    {
                        for item in items_
                        {
                            if let name = item as? NSDictionary
                            {
                                if let href = name.value(forKey: "href") as? String
                                {
                                    self.informationsenors(href: href,int: items_.count)
                                }
                            }
                        }
                    }
                }
            }
            if error == nil {
                // Ce que vous voulez faire.
            }
        }
        requestAPI.resume()
    }
    
    func informationsenors (href : String,int : Int)
    {
        let url = href
        print(href)
        let request = NSMutableURLRequest(url: URL(string: url)!)
        request.httpMethod = "GET"
        
        let requestAPI = URLSession.shared.dataTask(with: request as URLRequest)
        {
            data, response, error in
            if (error != nil) {
                print(error!.localizedDescription) // On indique dans la console ou est le problème dans la requête
            }
            let responseAPI = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            //print("responseString = \(responseAPI)") // Affiche dans la console la réponse de l'API
            if let jsonObj = try? JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
            {
                //printing the json in console
                //print(jsonObj!.value(forKey: "_links")!)
                var info : String = ""
                        if let items = jsonObj!.value(forKey: "metric") as? String
                        {
                            info += items
                        }
                        if let value = jsonObj!.value(forKey: "value") as? Double
                        {
                            info += " : " + String(value)
                        }
                        if let unite = jsonObj!.value(forKey: "unit") as? String
                        {
                             info += " " + unite
                        }
                    print(info)
                self.information.append(info)
                if(self.information.count==int)
                {
                    DispatchQueue.main.async(execute: {
                    self.viewtidmarsh = UIView(frame: CGRect.init(x: 0, y: 0, width: 400, height: 250))
                        self.viewtidmarsh.backgroundColor = UIColor.white
                        self.viewtidmarsh.layer.cornerRadius = 6
                        let lbl1 = UILabel(frame: CGRect.init(x: 8, y: 8, width: (self.viewtidmarsh.frame.size.width) - 16, height: 15))
                        let lbl2 = UILabel(frame: CGRect.init(x: lbl1.frame.origin.x, y: lbl1.frame.origin.y + lbl1.frame.size.height + 3, width: (self.viewtidmarsh.frame.size.width) - 16, height: 15))
                    self.printinfo = ""
                    print()
                    print()
                    for i in 0..<self.information.count
                    {
                        self.printinfo += self.information[i] + "\n"
                        print(self.information[i])
                    }
                    for i in 0..<self.DeviceTidmarsh.count
                    {
                        if self.DeviceTidmarsh[i].name.range(of: (self.mp) ) != nil
                        {
                            lbl2.text = self.DeviceTidmarsh[i].name
                        }
                    }
                    lbl2.font = UIFont.systemFont(ofSize: 14, weight: .light)
                    self.viewtidmarsh.addSubview(lbl2)
                    lbl1.text = "Tidmarsh Device"
                    self.viewtidmarsh.addSubview(lbl1)
                    let textsensors = UILabel(frame: CGRect.init(x: 5, y: 50, width: 290, height:140 ))
                    textsensors.text = self.printinfo
                    textsensors.font = UIFont.systemFont(ofSize: 14, weight: .light)
                    textsensors.numberOfLines=100
                    self.viewtidmarsh.addSubview(textsensors)
                    let button = UIButton(frame: CGRect.init(x: 50, y: 200, width: 100, height: 40))
                    button.addTarget(self, action: #selector(ViewController.playTapped(_:)), for: UIControlEvents.touchUpInside)
                    button.backgroundColor=UIColor.lightGray
                    button.setTitle("Stats", for: UIControlState.normal)
                    button.tintColor = UIColor.black
                    self.viewtidmarsh.addSubview(button)
                    self.view.addSubview(self.viewtidmarsh)
                    })
                }
            }
        }
        requestAPI.resume()
    }
    
    @objc func playTapped(_ sender:UIButton!)
    {
        print("DUDU")
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let nextViewController = storyBoard.instantiateViewController(withIdentifier: "TidmarshDeviceID") as! TidmarshDeviceViewController
        show(nextViewController, sender: self)
    }
}
