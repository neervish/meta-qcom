# Add a custom task to modify the partitions.conf file
do_modify_conf() {
    # Path to the specific partitions.conf file
    CONF_FILE="${S}/platforms/qcs6490-rb3gen2/ufs/partitions.conf"
    
    # Check if the file exists
    if [ -f "$CONF_FILE" ]; then
        # Check if the efi_recovery entry already exists to avoid duplicates
        if ! grep -q "name=efi_recovery" "$CONF_FILE"; then
            # Use sed to add the new line after the specified line
            sed -i '/--partition --lun=0 --name=efi --size=524288KB --type-guid=C12A7328-F81F-11D2-BA4B-00A0C93EC93B --filename=efi.bin/a --partition --lun=0 --name=efi_recovery --size=524288KB --type-guid=77036CD4-03D5-42BB-8ED1-37E5A88BAA34 --filename=efi_recovery.bin' "$CONF_FILE"
            bbwarn "Added efi_recovery partition to $CONF_FILE"
        else
            bbwarn "efi_recovery partition already exists in $CONF_FILE"
        fi
    else
        bbwarn "Could not find $CONF_FILE"
    fi
}

# Make the task run after patch but before configure
addtask modify_conf after do_patch before do_configure

