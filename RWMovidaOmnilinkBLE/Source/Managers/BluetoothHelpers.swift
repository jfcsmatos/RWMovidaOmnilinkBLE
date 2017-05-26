//
//  BluetoothHelpers.swift
//  PocMovidaBLE
//
//  Created by Joao Felipe on 5/24/17.
//  Copyright © 2017 João Felipe. All rights reserved.
//

import UIKit

class BluetoothHelpers: NSObject {

    private var SERVICE_NAME: String?
    private var SERVICE_UUID: String?
    private var CHARACTERISTC_UUID: String?
    
    public static let sharedInstance: BluetoothHelpers = BluetoothHelpers()
    
    public override init() {
        super.init()
        
    }
    
    func convertFrameToData(frame: Array<Int>) -> Data {
        var arrayInt8 : Array<UInt8> = []
        
        for i in frame {
            
            arrayInt8.append(UInt8(i))
        }
        
        return Data(arrayInt8)
    }
    
    func parseFrame(message: Array<Int>) -> Array<Array<Int>> {
        var packages : Array<Array<Int>> = []
        var arrayOfPackage : Array<Int> = []
        
        for i in message {
            
            if arrayOfPackage.count == 20 {
                packages.append(arrayOfPackage)
                arrayOfPackage = []
            }
            
            if i == message.last {
                arrayOfPackage.append(i)
                packages.append(arrayOfPackage)
                arrayOfPackage = []
                break
            }
            
            arrayOfPackage.append(i)
        }
        
        return packages
    }
    
    func parseCharacteristicValueData(bytes: Data) -> String {
        
        let byteArray = [UInt8](bytes)
        let arrayForParse = NSMutableArray()
        
        for i in 0..<byteArray.count {
            arrayForParse.add(Int(byteArray[i]))
        }
        
        return OmnilinkProtocol.parseMessage(buffer: arrayForParse, size: arrayForParse.count)
    }
    
}
